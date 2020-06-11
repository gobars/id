package com.github.gobars.id;

import org.junit.Assert;
import org.junit.Test;

public class PerfTest {
  public static final int SIZE = 100000;

  @Test
  public void perf() {
    long start = System.currentTimeMillis();
    long last = 0;
    for (int i = 0; i < SIZE; i++) {
      long n = Id.next();
      if (n <= last) {
        Assert.fail("i:" + i);
      }
      last = n;
    }

    // 39ms 100000
    System.out.println((System.currentTimeMillis() - start) + "ms " + SIZE);
  }
}
