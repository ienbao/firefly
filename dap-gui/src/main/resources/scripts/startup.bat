@echo off
set R_HOME=%~dp0R
set JRE_HOME=%~dp0jre
set PATH=%JRE_HOME%\bin;%JRE_HOME%\bin\server;%R_HOME%\bin\x64;%PATH%;%HOME%\bin
set CLASSPATH=%CLASSPATH%;%JRE_HOME%\lib;%R_HOME%\library\rJava\jri

set APP_JAR=dap-gui-2.5.0-SNAPSHOT.jar
set LOG=log

set ID=
    IF "%1%"=="start" GOTO start
    IF "%1%"=="stop" GOTO stop
    IF "%1%"=="restart" GOTO restart
    IF "%1%"=="exit" EXIT

:start
    call :startApp
    GOTO :eof

:stop
    call :stopApp
    GOTO :eof

:restart
    call :stopApp
    call :startApp
    GOTO :eof

:startApp

    IF NOT EXIST "%APP_JAR%" (
    call :stopApp
    )

    IF NOT EXIST "%LOG%" (
        ECHO "%LOG%" is not exist.
        md "%~dp0%LOG%"
        ECHO New "%LOG%" success.
    )

    set now=%date:~,4%%date:~5,2%%date:~8,2%
    echo iSPC is running...
	java -Djava.library.path="%R_HOME%\library\rJava\jri" -jar %APP_JAR% >> log/ispc_%now%.log
:stopApp
    taskkill /F /IM java > nul