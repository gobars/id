package com.github.gobars.id;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PerfTest {
  public static final int SIZE = 100000;

  @Test
  public void perf() {
    long start = System.currentTimeMillis();
    long last = 0;
    for (int i = 0; i < SIZE; i++) {
      long n = Id.next();
      assertTrue(n > last);
      last = n;
    }

    // 56ms 100000
    System.out.println((System.currentTimeMillis() - start) + "ms " + SIZE);
  }
}
