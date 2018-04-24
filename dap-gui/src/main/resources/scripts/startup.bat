@echo off
set R_HOME=%~dp0R
set JRE_HOME=%~dp0jre
set PATH=%JRE_HOME%\bin;%JRE_HOME%\bin\server;%R_HOME%\bin\x64;%PATH%;%HOME%\bin
set CLASSPATH=%CLASSPATH%;%JRE_HOME%\lib;%R_HOME%\library\rJava\jri

set APP_JAR=dap-gui-2.5.0-SNAPSHOT.jar
set LOG=log
set flag=0
set ID=
    IF "%1%"=="start" GOTO start
    IF "%1%"=="stop" GOTO stop
    IF "%1%"=="restart" GOTO restart
    IF "%1%"=="exit" EXIT

:start
        for /f "tokens=5" %%i in ('netstat -aon ^| findstr ":27018"') do (
            echo exit starting mongodb
            set flag=1
         )
         echo if exit starting mongodb: %flag%
        IF %flag%==0 (
           echo "start......"
           call :startMongodb
           call :startApp
            GOTO :eof
       )else (
           echo DAP has been running.
       )
       exit

:stop
    echo "stop......"
    call :stopApp
    GOTO :eof
    exit

:restart
    echo "restart......"
    call :stopApp
    call :startApp
    GOTO :eof

:startMongodb
    echo "start mongo"
    start installmongo.bat start

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
    echo DAP is running...
	java -Djava.library.path="%R_HOME%\library\rJava\jri" -jar %APP_JAR%
    exit

:stopApp
    echo "kill java....."
    taskkill /F /IM java > nul