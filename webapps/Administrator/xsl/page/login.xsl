<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout.xsl"/>

    <xsl:template match="/request">
        <xsl:call-template name="layout"/>
    </xsl:template>

    <xsl:template name="main-header">
        <header class="header navbar navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <img class="brand-logo" alt="logo" src="{$APP_LOGO_IMG_SRC}"/>
                    <span class="brand-title">
                        <xsl:value-of select="$APP_NAME"/>
                    </span>
                </div>
            </div>
        </header>
    </xsl:template>

    <xsl:template name="_content">
        <form class="sign-in" action="Login" method="post">
            <h1>
                Sign In
            </h1>
            <label class="login">
                <i class="fa fa-user"></i>
                <input type="text" name="login" value="" required="required" placeholder="Login"/>
                 
            </label>
            <label class="pwd">
                <i class="fa fa-lock"></i>
                <input type="password" name="pwd" value="" required="required" placeholder="Password"/>                   
            </label>
            <label class="noauth">
                <input type="checkbox" name="noauth" value="1"/>
                <span>
                   Another computer
                </span>
            </label>
            <button class="btn" type="submit">
                Log In
            </button>
        </form>
    </xsl:template>

</xsl:stylesheet>
