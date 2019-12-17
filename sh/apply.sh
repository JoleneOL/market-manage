#!/usr/bin/env bash

# 将数据恢复至本地mysql market 帐号需为root 密码需为空
# usage: apply.sh [sql]
# IF NOT EXISTS
# reset master;
# CREATE USER 'market'@'%';
mysql -h localhost -u root <<EOF
DROP DATABASE  IF EXISTS  market;

create database market;

EOF

mysql -h localhost -u root market < $1

