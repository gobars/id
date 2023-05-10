create schema id;
use id;
create table worker_id
(
    id       bigint auto_increment primary key,
    created  datetime default current_timestamp,
    ip       varchar(60),
    hostname varchar(60),
    pid      int,
    reason   varchar(60),
    biz      varchar(60)
)engine=innodb default charset=utf8mb4;
create table t_seq
(
    name        varchar(60) primary key comment '序列名称，不用的序列，请使用不用的名字',
    start       bigint   default 0     not null comment 'SEQ 起始值，用于重新循环',
    seq         bigint   default 0     not null comment '当前的SEQ取值',
    step        int      default 10000 not null comment '步长',
    water_level int      default 5000  not null comment '低于多少水位线需要补充',
    cycle       tinyint  default 0     not null comment '是否允许循环，必须max_seq同时设置时才生效',
    max_seq     bigint   default 0     not null comment '允许最大的序列值，0 时不校验',
    created     datetime default current_timestamp comment '创建时间',
    updated     datetime on update current_timestamp comment '更新时间'
) engine = innodb default charset = utf8mb4 comment '序列取号表';
create table t_worker_id
(
    name    varchar(60) primary key,
    value   bigint   default 0 not null,
    step    int      default 0 not null,
    created datetime default current_timestamp,
    updated datetime
        on update current_timestamp
) engine = innodb default charset = utf8mb4 comment 'worker_id 行记录取号表';