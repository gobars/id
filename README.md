# id

[![Build Status](https://travis-ci.org/gobars/id.svg?branch=master)](https://travis-ci.org/gobars/id)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.gobars%3Aid&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.github.gobars%3Aid)
[![Coverage Status](https://coveralls.io/repos/github/gobars/id/badge.svg?branch=master)](https://coveralls.io/github/gobars/id?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gobars/id/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.gobars/id/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

id generator with time backward-compatible.

## usage

```java
import com.github.gobars.Id;

long bizID=Id.next();
```

## 基于数据库`worker_id`表获得一次性workerId

Spring 配置:

```java

@Configuration
public class SpringAppConfig {
    @Bean
    public DataSource getDataSource() {
        val dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/id?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=yes&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        return dataSource;
    }

    @Bean
    public IdNext idNext(@Autowired DataSource dataSource) {
        val connGetter = new ConnGetter.DsConnGetter(dataSource);
        val workerIdDb = new WorkerIdDb().table("worker_id").connGetter(connGetter).biz("default");
        val spec = "epoch=20200603,timestampBits=41,backwardBits=0,workerBits=10,seqBits=12,roundMs=1";
        return new SnowflakeDb(Conf.fromSpec(spec), workerIdDb);
    }
}
```

Spec说明：

Spec | 值格式 | 默认值 | 说明
---  | --- | --- | --- 
epoch| yyyyMMdd|20200603|服务器第一次上线时间点
timestampBits|整型|41| 时间戳占用比特位数
roundMs|整型|1|时间戳规整到的时间单位(毫秒)
backwardBits|整型|0|时间回拨序号占用比特位数
workerBits|整型|10|worker占用比特位数
seqBits|整型|12|自增序号占用比特位数
maxBackwardSleepMs|整型|1000| 最大时间回拨
timestampBy|cache/system/nano|cache|时间戳计算算法 cache: SystemClock.now(), system: System.currentMillis(),nano: System.nanoTime()

使用:

```java

@Bean
public class XXService {
    @Autowired
    IdNext id;

    public void business() {
        // ...
        long bizID = id.next();
        // ...
    }
}
```

## WorkerId 获取顺序

1. 系统属性或者环境变量 `WORKERID`
1. hostname命名的最后数字部分，例如app-11中的11
1. IPv4或者IPv6最后一个Byte
1. 随机获取

## 原理

snowflake 改进:

API      |sign   | timestamp  |backwardId | workerId  | sequence       |max                 | limit    |years                        |remark
:---:    |:---:  | :---:      | :---:     | :---:     |  :---:         |:---:               |:---:     |:---:                        |:---
\-       |符号位  | 时间戳      |时间回拨序号 | 工作机器ID  |同时间戳内产生的序列|最大值               |限制       |使用年限                      | 备注
Id.next()|1 bit  | 41 bit（ms)| 2 bit     | 8 bit     | 12 bit          |2^63                |4096/ms   |2^41/1000/60/60/24/365.5≈69年| 标准snowflake中10位workerId抽出2位作为时间回拨序号
Id12.next()|1 bit  | 29 bit (s) | 1 bit    | 3 bit      | 6 bit          | 2^39=549,755,813,888|64/s      |2^29/60/60/24/365.5 ≈17年    | 产生最大12位长度数字的ID

时间回拨问题解决方案

1. `最新当前时间` - `上次获取的时间` <= 1s， 直接等待1s
1. `最新当前时间` < `上次获取的时间`，切换backwardId，并且存储到 `~/.worker.backwardId.108`中，其中 `108`表示workerId.

不能解决以下极端条件：

1. 程序启动后，停机时间回拨（程序无法感知）
1. 程序运行中，连续4次时间回拨（超过最大回拨序号）

## 资料

1. [美团技术分享：深度解密美团的分布式ID生成算法](https://zhuanlan.zhihu.com/p/83753710)
1. [时钟回拨问题咋解决？百度开源的唯一ID生成器UidGenerator](https://zhuanlan.zhihu.com/p/77737855)
1. [分布式ID增强篇--优化时钟回拨问题](https://www.jianshu.com/p/98c202f64652)

## 脚本

### MySQL

```sql
drop table if exists t_seq;
create table t_seq
(
    name        varchar(60) primary key comment '序列名称，不用的序列，请使用不用的名字',
    start       bigint   default 0     not null comment 'SEQ 起始值，用于重新循环',
    seq         bigint   default 0     not null comment '当前的SEQ取值',
    step        int      default 10 not null comment '步长',
    water_level int      default 5  not null comment '低于多少水位线需要补充',
    cycle       tinyint  default 0     not null comment '是否允许循环，必须max_seq同时设置时才生效',
    max_seq     bigint   default 0     not null comment '允许最大的序列值，0 时不校验',
    created     datetime default current_timestamp comment '创建时间',
    updated     datetime on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment '序列取号表';
```

### MySQL

```sql
drop table if exists worker_id;
create table worker_id
(
    id       bigint auto_increment primary key comment 'worker id',
    created  datetime default current_timestamp comment '创建时间',
    ip       varchar(60) comment '当前机器IP',
    hostname varchar(60) comment '当前机器名称',
    pid      int comment '应用程序PID',
    reason   varchar(60) comment '申请原因 start:启动 backwards:时间回拨',
    biz      varchar(60) comment '当前业务名称'
) engine = innodb
  default charset = utf8mb4 comment 'worker id 分配表';
```

or online format:

```sql
drop table if exists worker_id;
create table worker_id
(
    id       bigint auto_increment primary key,
    created  datetime default current_timestamp,
    ip       varchar(60),
    hostname varchar(60),
    pid      int,
    reason   varchar(60),
    biz      varchar(60)
)engine = innodb default charset = utf8mb4;
```

### Oracle

```sql
drop table worker_id;
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

comment
on table worker_id IS 'worker id 分配表';

comment
on column worker_id.id IS 'worker id';
comment
on column worker_id.created IS '创建时间';
comment
on column worker_id.ip IS '当前机器IP';
comment
on column worker_id.hostname IS '当前机器名称';
comment
on column worker_id.pid IS '应用程序PID';
comment
on column worker_id.reason IS '申请原因 start:启动 backwards:时间回拨';
comment
on column worker_id.biz IS '当前业务名称';

CREATE SEQUENCE worker_id_seq
    START WITH 1
    INCREMENT BY 1 CACHE 100;

CREATE
OR REPLACE TRIGGER trigger_worker_id_seq
    BEFORE INSERT
    ON worker_id
    FOR EACH ROW
BEGIN
SELECT worker_id_seq.nextval
INTO :new.id
FROM dual;
END;

```