<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout.xsl"/>

    <xsl:template match="/request">
        <xsl:call-template name="layout"/>
    </xsl:template>

    <xsl:template name="_content">
        <xsl:apply-templates select="//document[@entity = 'pagerule']"/>
    </xsl:template>

    <xsl:template match="document">
        <form name="{@entity}" action="" data-edit="{@editable}">
            <header class="content-header">
                <h1 class="header-title">
                    <span>Page rule</span>
                    <span class="text-muted">
                        <xsl:value-of select="concat(' ', @docid)"/>
                    </span>
                </h1>
                <div class="content-actions">
                    <xsl:apply-templates select="//actionbar"/>
                </div>
            </header>
            <section class="content-body">
                <fieldset class="fieldset">
                    <div class="form-group">
                        <div class="control-label">
                            is on
                        </div>
                        <div class="controls">
                            <div class="input-placeholder">
                                <input type="checkbox" name="ison" value="1">
                                    <xsl:if test="fields/ison = 'ON'">
                                        <xsl:attribute name="checked" select="'checked'"/>
                                    </xsl:if>
                                </input>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            xslt
                        </div>
                        <div class="controls">
                            <input type="text" name="xslt" value="{fields/xslt}" class="span8"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            is secured
                        </div>
                        <div class="controls">
                            <div class="input-placeholder">
                                <input type="checkbox" name="issecured" value="1">
                                    <xsl:if test="fields/issecured = 'true'">
                                        <xsl:attribute name="checked" select="'checked'"/>
                                    </xsl:if>
                                </input>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            caching
                        </div>
                        <div class="controls">
                            <input type="text" name="caching" value="{fields/caching}" class="span8"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="control-label">
                            Description
                        </div>
                        <div class="controls">
                            <textarea name="description" class="span8">
                                <xsl:value-of select="fields/description"/>
                            </textarea>
                        </div>
                    </div>
                </fieldset>
            </section>
            <input type="hidden" name="fsid" value="{//response/content/fsid}"/>
        </form>
    </xsl:template>

</xsl:stylesheet>
