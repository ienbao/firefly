@echo off

set MONGO=%~dp0mongodb

IF EXIST "%MONGO%" (
cd %MONGO%\bin
net stop MongoDB
mongod --remove MongoDB
mongod --repair --dbpath %MONGO%\data\db
mongod --config %MONGO%\data\log\mongodb.log --install --serviceName "MongoDB"
net start MongoDB
)