<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout.xsl"/>

    <xsl:template match="/request">
        <xsl:call-template name="layout">
            <xsl:with-param name="include_head">
                <script type="text/javascript">
                    <![CDATA[
                    function createCookie(name, value, days) {
                        var expires;

                        if (days) {
                            var date = new Date();
                            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                            expires = '; expires=' + date.toGMTString();
                        } else {
                            expires = '';
                        }
                        document.cookie = encodeURIComponent(name) + '=' + encodeURIComponent(value) + expires + '; path=/';
                    }

                    function changeEventHandler(el) {
                        var data = {};
                        data[el.name] = el.value;

                        var xhrArgs = {
                            cache: false,
                            type: 'POST',
                            dataType: 'json',
                            url: 'Provider?id=change-session-val-action',
                            data: data,
                            success: function(res) {
                                createCookie("activetab", "#tabs-3", 1)
                                window.location.reload(false);
                            }
                        };
                        return nb.ajax(xhrArgs);
                    }

                    function readCookie(name) {
                        var nameEQ = encodeURIComponent(name) + "=";
                        var ca = document.cookie.split(';');
                        for (var i = 0; i < ca.length; i++) {
                            var c = ca[i];
                            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
                            if (c.indexOf(nameEQ) === 0) return decodeURIComponent(c.substring(nameEQ.length, c.length));
                        }
                        return null;
                    }

                    function eraseCookie(name) {
                        createCookie(name, "", -1);
                    }

                    $(document).ready(function(){
                        var tab = readCookie("activetab");
                        if(tab){
                            $(".nav").find("a[href="+tab+"]").click();
                            eraseCookie("activetab")
                        }

                        $('select[name=pagesize], select[name=lang]').on('change', function() {
                            changeEventHandler(this);
                        });
                    }); ]]>
                </script>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="_content">
        <xsl:apply-templates select="//document[@entity = 'employee']"/>
    </xsl:template>

    <xsl:template match="document[@entity = 'employee']">
        <form name="{@entity}" class="form form-userprofile" action="" autocomplete="off">
            <header class="content-header">
                <h1 class="header-title">
                    <xsl:value-of select="concat('Employee', ' - ', fields/name)"/>
                </h1>
                <div class="content-actions">
                    <xsl:apply-templates select="//actionbar"/>
                </div>
            </header>
            <section class="content-body">
                <ul class="nav nav-tabs" role="tablist">
                    <li class="active">
                        <a href="#tabs-1" role="tab" data-toggle="tab">
                            Properties
                        </a>
                    </li>
                   <!-- <li>
                        <a href="#tabs-3" role="tab" data-toggle="tab">
                            Iinterface
                        </a>
                    </li>-->
                </ul>
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="tabs-1">
                        <!--<fieldset class="fieldset fieldset-user-avatar">
                            <img class="user-avatar" src="img/avatar.png"/>
                        </fieldset>-->
                        <fieldset class="fieldset fieldset-user-fields">
                            <div class="form-group">
                                <div class="control-label">
                                    User name
                                </div>
                                <div class="controls">
                                    <span class="input-placeholder">
                                        <xsl:value-of select="fields/name"/>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Login
                                </div>
                                <div class="controls">
                                    <input type="text" name="login" value="{fields/login}" class="span3"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Password
                                </div>
                                <div class="controls">
                                    <input type="password" name="pwd" class="span3"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                   Confirm password
                                </div>
                                <div class="controls">
                                    <input type="password" name="pwd_confirm" class="span3"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                   E-mail
                                </div>
                                <div class="controls">
                                    <input type="text" name="email" value="{fields/email}" class="span3"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Organization
                                </div>
                                <div class="controls">
                                    <span class="input-placeholder">
                                        <xsl:value-of select="fields/organization"/>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Department
                                </div>
                                <div class="controls">
                                    <span class="input-placeholder">
                                        <xsl:value-of select="fields/department"/>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                   Position
                                </div>
                                <div class="controls">
                                    <span class="input-placeholder">
                                        <xsl:value-of select="fields/position"/>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Roles
                                </div>
                                <div class="controls">
                                    <ul class="input-placeholder list-style-none">
                                        <xsl:for-each select="fields/roles/entry">
                                            <li>
                                                <xsl:value-of select="."/>
                                            </li>
                                        </xsl:for-each>
                                    </ul>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="tabs-3">
                        <fieldset class="fieldset">
                            <div class="form-group">
                                <div class="control-label">
                                    An amount of documents is in a kind
                                </div>
                                <div class="controls">
                                    <select name="pagesize" class="native span2">
                                        <option value="10">
                                            <xsl:if test="//pagesize = '10'">
                                                <xsl:attribute name="selected" select="'selected'"/>
                                            </xsl:if>
                                            10
                                        </option>
                                        <option value="20">
                                            <xsl:if test="//pagesize = '20'">
                                                <xsl:attribute name="selected" select="'selected'"/>
                                            </xsl:if>
                                            20
                                        </option>
                                        <option value="30">
                                            <xsl:if test="//pagesize = '30'">
                                                <xsl:attribute name="selected" select="'selected'"/>
                                            </xsl:if>
                                            30
                                        </option>
                                        <option value="50">
                                            <xsl:if test="//pagesize = '50'">
                                                <xsl:attribute name="selected" select="'selected'"/>
                                            </xsl:if>
                                            50
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label">
                                    Interface language
                                </div>
                                <div class="controls">
                                    <xsl:variable name="currentlang" select="/request/@lang"/>
                                    <select name="lang" class="native span2">
                                        <xsl:for-each select="//query[@entity = 'language']/entry">
                                            <option value="{viewcontent/lang/@id}">
                                                <xsl:if test="viewcontent/lang/@id = $currentlang">
                                                    <xsl:attribute name="selected" select="'selected'"/>
                                                </xsl:if>
                                                <xsl:value-of select="viewcontent/lang"/>
                                            </option>
                                        </xsl:for-each>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="control-label"></div>
                                <div class="controls">
                                    <a href="javascript:void(0)" data-toggle-theme="theme1" class="input-placeholder">
                                        <xsl:value-of select="//captions/change_skin/@caption"/>
                                    </a>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </section>
        </form>
    </xsl:template>

</xsl:stylesheet>
