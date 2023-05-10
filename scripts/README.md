1. [has-oracle-started.sql](https://github.com/plmwong/docker-oracle-xe-11g-soa/blob/master/has-oracle-started.sql)

2. Docker 安装 postgresql

    ```shell
    docker pull postgres:12.3
    docker run --name postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:12.3
    docker exec -it postgres psql -U postgres -c "create database id;"
    docker exec -it postgres psql -U postgres -c "create table worker_id (id serial primary key, created timestamp default current_timestamp, ip varchar(60), hostname varchar(60), pid int, reason varchar(60), biz varchar(60));"
    docker exec -it postgres psql -U postgres -c "create table t_seq (name varchar(60) primary key, start bigint default 0 not null, seq bigint default 0 not null, step int default 10000 not null, water_level int default 5000 not null, cycle int4 default 0 not null, max_seq bigint default 0 not null, created timestamp default current_timestamp, updated timestamp default current_timestamp);"
    docker exec -it postgres psql -U postgres -c "create table t_worker_id (name varchar(60) primary key, value int default 0, step int default 0, created timestamp default current_timestamp, updated timestamp default current_timestamp);"
    ```      
