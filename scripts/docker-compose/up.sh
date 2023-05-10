#!/bin/bash

BASE_PATH=$(
  cd "$(dirname "$0")"
  pwd
)
cd $BASE_PATH
# Define the directory containing the Docker Compose file
DOCKER_COMPOSE_DIR="$BASE_PATH"

# Start the MySQL container
docker-compose -f ${DOCKER_COMPOSE_DIR}/docker-compose.yml up -d

# Wait for the container to start
sleep 30s

# Create the database and table
docker exec -i dbs-mysql mysql -uroot -p'root' < ${DOCKER_COMPOSE_DIR}/init/mysql.sql
docker exec -i dbs-postgres psql -U postgres < ${DOCKER_COMPOSE_DIR}/init/postgres.sql
docker exec -i dbs-oracle11g sh /init/oracle/wait-for-oracle.sh

#docker exec -it dbs-mysql mysqladmin ping -uroot -proot --wait=30 --silent
#docker exec -it dbs-mysql mysql -u root -p'root' -s -e "create database id;create table id.worker_id(id bigint auto_increment primary key,created datetime default current_timestamp,ip varchar(60),hostname varchar(60),pid int,reason varchar(60),biz varchar(60))engine=innodb default charset=utf8mb4;create table id.t_seq ( name        varchar(60) primary key comment '序列名称，不用的序列，请使用不用的名字', start       bigint   default 0     not null comment 'SEQ 起始值，用于重新循环', seq         bigint   default 0     not null comment '当前的SEQ取值', step  int default 10000 not null comment '步长', water_level int default 5000  not null comment '低于多少水位线需要补充', cycle  tinyint  default 0  not null comment '是否允许循环，必须max_seq同时设置时才生效', max_seq     bigint   default 0     not null comment '允许最大的序列值，0 时不校验', created     datetime default current_timestamp comment '创建时间', updated     datetime on update current_timestamp comment '更新时间' ) engine = innodb default charset = utf8mb4 comment '序列取号表';"
#docker exec -it dbs-mysql mysql -u root -p'root' -s -e "create table id.create table t_worker_id ( name varchar(60) primary key, value bigint default 0 not null, step int default 0 not null, created datetime default current_timestamp, updated datetime on update current_timestamp ) engine = innodb default charset = utf8mb4 comment 'worker_id 行记录取号表';"
#docker exec -it dbs-oracle11g /bin/bash /data/wait-for-oracle.sh
#docker exec -it dbs-postgres psql -U postgres -c "create database id;"
#docker exec -it dbs-postgres psql -U postgres -c "create table worker_id (id serial primary key, created timestamp default current_timestamp, ip       varchar(60), hostname varchar(60), pid      int, reason   varchar(60), biz      varchar(60));"
#docker exec -it dbs-postgres psql -U postgres -c "create table t_seq (name varchar(60) primary key, start bigint default 0 not null, seq         bigint    default 0     not null, step        int       default 10000 not null, water_level int       default 5000  not null, cycle       int4      default 0     not null, max_seq     bigint    default 0     not null, created     timestamp default current_timestamp, updated     timestamp default current_timestamp);"
#docker exec -it dbs-postgres psql -U postgres -c "create table t_worker_id (name varchar(60) primary key, value int default 0, step    int       default 0, created     timestamp default current_timestamp, updated     timestamp default current_timestamp);"
