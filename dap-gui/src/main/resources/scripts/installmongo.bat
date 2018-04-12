@echo off

set MONGO=%~dp0mongodb

IF EXIST "%MONGO%" (
cd "%MONGO%\bin"
mongod --dbpath="%MONGO%\data\db" --logpath="%MONGO%\data\log\mongo.log"
)