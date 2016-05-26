@echo off
REM Author Medet

set HOME=%~dp0
set LIBS=%HOME%libs/
set JAVA_START_OPTS=-Xms128m -Xmx512m
set MAIN_CLASS=com.exponentus.server.Server
set CLASSPATH=.
set _JAVACMD=java

TITLE NextBase application

cd %HOME%

FOR /R %LIBS% %%G IN (*.jar) DO (call :list_classpath %%G)
set CLASSPATH=%CLASSPATH:\=/%
goto startJava
:list_classpath
	set CLASSPATH=%CLASSPATH%;%1
	exit /b

:startJava
echo %_JAVACMD% %JAVA_START_OPTS% -classpath %CLASSPATH% %MAIN_CLASS%
%_JAVACMD% %JAVA_START_OPTS% -classpath %CLASSPATH% %MAIN_CLASS%

if errorlevel 1 pause