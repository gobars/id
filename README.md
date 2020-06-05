# id

[![Build Status](https://travis-ci.org/gobars/id.svg?branch=master)](https://travis-ci.org/gobars/id)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.gobars%3Aid&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.github.gobars%3Aid)
[![Coverage Status](https://coveralls.io/repos/github/gobars/id/badge.svg?branch=master)](https://coveralls.io/github/gobars/id?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gobars/id/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.gobars/id/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

id generator with time backward-compatible.

## usage

```xml
 <dependency>
    <groupId>com.github.gobars</groupId>
    <artifactId>id</artifactId>
    <version>0.0.5-SNAPSHOT</version>
 </dependency>
```

```java
import com.github.gobars.Id;

long bizID = Id.next();
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
