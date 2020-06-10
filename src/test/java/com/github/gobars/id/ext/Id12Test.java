package com.github.gobars.id.ext;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Id12Test {
  public static final int SIZE = 65;

  @Test
  public void next() {
    long next = Id12.next();
    System.out.println("Id12: " + next);
    assertTrue(String.valueOf(next).length() <= 12);
  }

  @Test
  public void perf() {
    long start = System.currentTimeMillis();
    long last = 0;
    for (int i = 0; i < SIZE; i++) {
      long n = Id12.next();
      if (n <= last) {
        Assert.fail(n + " <= " + last + " at i = " + i);
      }

      last = n;
    }

    // 10s 6ms 130
    System.out.println((System.currentTimeMillis() - start) + "ms " + SIZE);
  }
}
