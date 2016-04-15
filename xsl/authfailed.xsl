<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="utf-8"/>

    <xsl:template match="/request/error">
        <html>
            <head>
                <title>Nextbase - Error</title>
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                <link rel="stylesheet" href="/SharedResources/nb/css/nb.min.css"/>
                <style>
                    <![CDATA[
                    body {
                       // background-image: url(/SharedResources/img/classic/f.gif);
                        font-family: arial;
                        padding-top: 5%;
                    }
                    table {
                        height: 500px;
                        width: 98%;
                    }
                    h1 { font-weight: normal; }
                    p { font-size:.85em; }
                    ul {
                        font-size: .9em;
                        list-style: square;
                        margin-top: 15px;
                    }
                    ul li { padding: .25em; }
                    footer {
                        display: inline-block;
                        font-size: 0.8em;
                        margin-top: -30px;
                        margin-left: 22%;
                        z-index: 1;
                    }
                    footer a { padding: .3em; }
                    ]]>
                </style>
            </head>
            <body>
                <table>
                    <tr>
                        <td style="padding-right:1.3em;text-align:right;width:20%">
                            <h1>NextBase</h1>
                            <p>
                                <xsl:value-of select="concat('Version ', version, ' &#169; Exponentus 2016')"/>
                            </p>
                        </td>
                        <td style="background:#ffcc00;width:2px"></td>
                        <td style="padding-left:1.3em">
                            <h1>Authorization error</h1>
                            <ul>
                               <!--  <li>Проверьте правильность написания имени пользователя</li>
                                <li>Убедитесь, что пароль вводится на том же языке, что и при регистрации</li>
                                <li>Убедитесь, не нажат ли [CapsLock]</li> -->
                                <li>
                                    <a href="Logout">Try again...</a>
                                </li>
                            </ul>
                        </td>
                    </tr>
                </table>
                <footer>
                    <a href="http://dev.exponentus.com" target="_blank">Exponentus</a>
                  
                </footer>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
