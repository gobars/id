package com.github.gobars.id.bench;

import com.github.gobars.id.util.SystemClock;
import org.junit.Test;

public class SystemClockTest {
  int N = 100000000;

  @Test
  public void currentTimeMillis() {
    long sum = 0;
    long t1 = System.currentTimeMillis();
    for (int i = 0; i < N; i++) sum += System.currentTimeMillis();
    long t2 = System.currentTimeMillis();
    System.out.println(
        "currentTimeMillis Sum = "
            + sum
            + "; time = "
            + (t2 - t1)
            + "; or "
            + (t2 - t1) * 1.0E6 / N
            + " ns / iter");
  }

  @Test
  public void systemClock() {
    long sum = 0;
    long t1 = SystemClock.now();
    for (int i = 0; i < N; i++) sum += SystemClock.now();
    long t2 = SystemClock.now();
    System.out.println(
        "systemClock Sum = "
            + sum
            + "; time = "
            + (t2 - t1)
            + "; or "
            + (t2 - t1) * 1.0E6 / N
            + " ns / iter");
  }

  @Test
  public void nanoTime() {
    long sum = 0;
    long t1 = System.nanoTime();
    for (int i = 0; i < N; i++) sum += System.nanoTime();
    long t2 = System.nanoTime();
    System.out.println(
            "nanoTime Sum = "
                    + sum
                    + "; time = "
                    + (t2 - t1)
                    + "; or "
                    + (t2 - t1) * 1.0E6 / N
                    + " ns / iter");
  }
}
