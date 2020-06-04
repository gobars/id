# id

id generator with time backward-compatible.

## usage

```xml
 <dependency>
    <groupId>com.github.gobars</groupId>
    <artifactId>id</artifactId>
    <version>0.0.1</version>
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

sign | timestamp |backwardId| workerId|sequence
:---:  | :---:      | :---: | :---:     |:---:
1bit | 41bit    |2 bit | 8 bit  |12 bit
符号位 | 时间戳    |时间回拨序号 | 工作机器ID  |同一个毫秒内产生的不同序列

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
