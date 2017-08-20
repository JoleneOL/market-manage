@echo off
rem 将数据恢复至本地mysql market 帐号需为root 密码需为空
rem usage: apply.bat [sql]
rem IF NOT EXISTS
mysql -h localhost -u root --execute="DROP DATABASE  IF EXISTS  market;CREATE USER 'market'@'%';create database market CHARACTER SET=UTF8;reset MASTER;"
mysql -h localhost -u root market < %1
