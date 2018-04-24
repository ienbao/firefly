@echo off

set MONGO=%~dp0mongodb
set LOG=log
set DATA=data
set DB=%DATA%\db
set flag=0
set ID=
    IF "%1%"=="start" GOTO start
    IF "%1%"=="stop" GOTO stop

:start
   for /f "tokens=5" %%i in ('netstat -aon ^| findstr ":27018"') do (
     echo exit starting mongodb
     set flag=1
  )
  echo if exit starting mongodb: %flag%
  IF EXIST "%MONGO%" ( IF %flag%==0 (

     IF NOT EXIST "%DB%" (
          ECHO "%DB%" is not exist.
          md "%~dp0%DB%"
          ECHO New "%DB%" success.
       )
       IF NOT EXIST "%LOG%" (
         ECHO "%LOG%" is not exist.
        md "%~dp0%LOG%"
        ECHO New "%LOG%" success.
      )


       cd "%MONGO%\bin"
       echo start mongod.exe
        mongod --dbpath="%~dp0%DB%" --port=27018 --logpath="%~dp0%LOG%\mongo.log" --wiredTigerCacheSizeGB 4
     ))
    exit


:stop
  for /f "tokens=5" %%i in ('netstat -aon ^| findstr ":27018"') do (
     echo kill mongod.exe pid %%i
     taskkill /f /pid %%i /T
  )
  exit


