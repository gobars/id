# id

id generator

## usage

```xml
 <dependency>
    <groupId>cn.bjca</groupId>
    <artifactId>id</artifactId>
    <version>0.0.1</version>
 </dependency>
```

```java
long bizID = Id.next();
```

## 原理

snowflake:

sign | timestamp | workerid|sequence
---  | ---       | ---     |---
1bit | 41bit     | 10 bit  |12 bit
