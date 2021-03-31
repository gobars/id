package com.github.gobars.id;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.Seq;
import lombok.val;
import org.junit.Test;

public class SeqTest {
  public static final int SIZE = 100000;

  @Test
  public void perf() throws InterruptedException {
    val ds = new DruidDataSource();
    ds.setDriverClassName("com.mysql.jdbc.Driver");
    ds.setUrl(
        "jdbc:mysql://192.168.126.182:3309/id?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=yes&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
    ds.setUsername("root");
    ds.setPassword("1qazzaq1");

    val connGetter = new ConnGetter.DsConnGetter(ds);

    final Seq seq = new Seq().connGetter(connGetter).table("t_seq").name("seq");
    long start = System.currentTimeMillis();

    int THREAD_SIZE = 20;
    Thread[] threads = new Thread[THREAD_SIZE];

    for (int j = 0; j < THREAD_SIZE; j++) {
      threads[j] =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  //                  List<Long> l = new ArrayList<Long>(SIZE);
                  for (int i = 0; i < SIZE; i++) {
                    /* long n = */ seq.next();
                    //                    l.add(n);
                  }

                  //                  for (Long n : l) {
                  //                    System.out.println(n);
                  //                  }
                }
              });
    }

    for (int j = 0; j < THREAD_SIZE; j++) {
      threads[j].start();
    }

    for (int j = 0; j < THREAD_SIZE; j++) {
      threads[j].join();
    }

    // 1479ms 2000000
    System.out.println(
        (System.currentTimeMillis() - start)
            + "ms "
            + SIZE
            + " sequences per "
            + THREAD_SIZE
            + " Threads");
  }
}
