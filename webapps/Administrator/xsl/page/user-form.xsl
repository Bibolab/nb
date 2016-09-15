<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout.xsl"/>

    <xsl:template match="/request">
        <xsl:call-template name="layout"/>
    </xsl:template>

    <xsl:template name="_content">
        <xsl:apply-templates select="//document[@entity = 'user']"/>
    </xsl:template>

    <xsl:template match="document[@entity]">
        <form name="{@entity}" action="" data-edit="{@editable}">
            <header class="content-header">
                <h1 class="header-title">
                    User <xsl:value-of select="fields/login"/>
                </h1>
                <div class="content-actions">
                    <xsl:apply-templates select="//actionbar"/>
                </div>
            </header>
            <section class="content-body">
                <fieldset class="fieldset">
                    <div class="form-group">
                        <div class="control-label">
                            Login
                        </div>
                        <div class="controls">
                            <input type="text" name="login" value="{fields/login}" class="span4" autofocus="true"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                           E-mail
                        </div>
                        <div class="controls">
                            <input type="email" name="email" value="{fields/email}" class="span4"/>
                            <a class="btn action_test_email" title="send test email message" href="#" data-msgtype="email" data-action="test_message_email">
                                <span>
                                    Test
                                </span>
                            </a>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                           XMPP
                        </div>
                        <div class="controls">
                            <input type="text" name="xmpp" value="{fields/xmpp}" class="span4"/>
                            <a class="btn action_test_xmpp" title="send test xmpp message" href="#" data-msgtype="xmpp" data-action="test_message_xmpp">
                                <span>
                                    Test
                                </span>
                            </a>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                           Slack
                        </div>
                        <div class="controls">
                            <input type="text" name="slack" value="{fields/slack}" class="span4"/>
                            <a class="btn action_test_slack" title="send test slack message" href="#" data-msgtype="slack" data-action="test_message_slack">
                                <span>
                                    Test
                                </span>
                            </a>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                           Password
                        </div>
                        <div class="controls">
                            <input type="password" name="pwd" value="" class="span3" autocomplete="off"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            Password comfirmation
                        </div>
                        <div class="controls">
                            <input type="password" name="pwd_confirm" value="" class="span3" autocomplete="off"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            Status
                        </div>
                        <div class="controls">
                            <input type="text" name="status" value="{fields/status}" class="span4" autofocus="false" disabled="disabled"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                           Default language
                        </div>
                        <div class="controls">
                            <xsl:variable name="currentlang" select="fields/defaultlang"/>
                            <select name="defaultlang" class="native span2">
                                <xsl:for-each select="//constants[@entity = 'languagecode']/entry">
                                    <option value="{@attrval}">
                                        <xsl:if test="@attrval = $currentlang">
                                            <xsl:attribute name="selected" select="'selected'"/>
                                        </xsl:if>
                                        <xsl:value-of select="."/>
                                    </option>
                                </xsl:for-each>

                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            Allowed applications
                        </div>
                        <div class="controls">
                            <ul class="list-style-none">
                                <xsl:variable name="apps" select="fields/apps"/>
                                <xsl:for-each select="//query[@entity = 'application']/entry">
                                    <li>
                                        <label>
                                            <input type="checkbox" name="app" value="{@id}" autocomplete="off">
                                                <xsl:if test="$apps/entry/@id = @id">
                                                    <xsl:attribute name="checked" select="'checked'"/>
                                                </xsl:if>
                                                <xsl:if test="viewcontent/app/@id = 'Workspace'">
                                                    <xsl:attribute name="checked" select="'checked'"/>
                                                    <xsl:attribute name="disabled" select="'disabled'"/>
                                                </xsl:if>
                                            </input>
                                            <xsl:if test="viewcontent/app/@id = 'Workspace'">
                                                <input type="hidden" name="app" value="{@id}"/>
                                            </xsl:if>
                                            <span>
                                                <xsl:value-of select="viewcontent/app"/>
                                            </span>
                                        </label>
                                    </li>
                                </xsl:for-each>
                            </ul>
                        </div>
                    </div>
                </fieldset>
            </section>
            <input type="hidden" name="fsid" value="{//response/content/fsid}"/>
        </form>
    </xsl:template>

</xsl:stylesheet>
