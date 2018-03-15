@echo off
cd /d %~dp0
cd %~dp0
set JRE_HOME=%~dp0jre
set PATH=%SystemRoot%\System32;%JRE_HOME%\bin;%JRE_HOME%\bin\server;%HOME%\bin
rem set PATH=%PATH%;%SystemRoot%;%SystemRoot%\system32;%SystemRoot%\System32\Wbem;%JRE_HOME%\bin;%JRE_HOME%\bin\server;%HOME%\bin
set CLASSPATH=%CLASSPATH%;%JRE_HOME%\lib

set APP_JAR=dap-gui-1.0.0.jar
set LOG=%~dp0log

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

	IF EXIST "%APP_JAR%" (
    java -Xmx5120M -Xms5120M -Xmn500M -Xss256K -XX:SurvivorRatio=3 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:MaxTenuringThreshold=3 -XX:AutoBoxCacheMax=20000 -XX:SoftRefLRUPolicyMSPerMB=200 -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods  -jar %APP_JAR% --spring.profiles.active=dev>> log/ispc_"%now%".log
    )

    IF NOT EXIST "%LOG%" (
        ECHO "%LOG%" is not exist.
        md %LOG%"
        ECHO New "%LOG%" success.
    )
   set now=%date:~,4%%date:~5,2%%date:~8,2%
   echo iSPC is running...
  rem where java
  rem   echo %APP_JAR%
  rem   echo %now%.log
  rem echo %log%
	 .\jre\bin\java  -jar %APP_JAR%  >> %log%/ispc_%now%.log
:stopApp
   taskkill /F /IM "java.exe" > nul
   taskkill /F /PID %result%

