-- drop table worker_id;
create table worker_id
(
    id       int primary key,
    created  timestamp default current_timestamp,
    ip       varchar2(60),
    hostname varchar2(60),
    pid      int,
    reason   varchar2(60),
    biz      varchar2(60)
);

comment on table worker_id IS 'worker id 分配表';

comment on column worker_id.id IS 'worker id';
comment on column worker_id.created IS '创建时间';
comment on column worker_id.ip IS '当前机器IP';
comment on column worker_id.hostname IS '当前机器名称';
comment on column worker_id.pid IS '应用程序PID';
comment on column worker_id.reason IS '申请原因 start:启动 backwards:时间回拨';
comment on column worker_id.biz IS '当前业务名称';

-- drop sequence worker_id_seq;
create sequence worker_id_seq
    start with 1
    increment by 1
    cache 100;

create or replace trigger trigger_worker_id_seq
    before insert
    on worker_id
    for each row
begin
    select worker_id_seq.nextval
    into :new.id
    from dual;
end;

/
