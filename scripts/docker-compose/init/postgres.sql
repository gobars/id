
create table worker_id
(
    id       serial
        primary key,
    created  timestamp default CURRENT_TIMESTAMP,
    ip       varchar(60),
    hostname varchar(60),
    pid      integer,
    reason   varchar(60),
    biz      varchar(60)
);

alter table worker_id
    owner to postgres;

create table t_seq
(
    name        varchar(60)             not null
        primary key,
    start       bigint    default 0     not null,
    seq         bigint    default 0     not null,
    step        integer   default 10000 not null,
    water_level integer   default 5000  not null,
    cycle       integer   default 0     not null,
    max_seq     bigint    default 0     not null,
    created     timestamp default CURRENT_TIMESTAMP,
    updated     timestamp default CURRENT_TIMESTAMP
);

alter table t_seq
    owner to postgres;

create table t_worker_id
(
    name    varchar(60) not null
        primary key,
    value   integer   default 0,
    step    integer   default 0,
    created timestamp default CURRENT_TIMESTAMP,
    updated timestamp default CURRENT_TIMESTAMP
);

alter table t_worker_id
    owner to postgres;

