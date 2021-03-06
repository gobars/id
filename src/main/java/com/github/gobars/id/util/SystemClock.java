package com.github.gobars.id.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Optimization of performance issues for System.currentTimeMillis() in high concurrency scenarios
 *
 * <p>The call of System.currentTimeMillis() is much more time-consuming than the new normal object
 * (the amount of time is higher than I have not tested, some say it is about 100 times)
 *
 * <p>System.currentTimeMillis() is slow because it has to deal with the system once
 *
 * <p>The background periodically updates the clock. When the JVM exits, the thread automatically
 * reclaims
 *
 * @author lry
 */
public class SystemClock {
  private final long period;
  private final AtomicLong now;

  private final ExecutorService scheduler;

  private SystemClock(long period) {
    this.period = period;
    this.now = new AtomicLong(System.currentTimeMillis());
    this.scheduler = scheduleClockUpdating();
  }

  public static long now() {
    return instance().currentTimeMillis();
  }

  private static SystemClock instance() {
    return InstanceHolder.INSTANCE;
  }

  private ExecutorService scheduleClockUpdating() {
    ScheduledExecutorService s =
        Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
              @Override
              public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "System Clock");
                t.setDaemon(true);
                return t;
              }
            });

    Runnable sr =
        new Runnable() {
          @Override
          public void run() {
            now.set(System.currentTimeMillis());
          }
        };
    s.scheduleAtFixedRate(sr, period, period, TimeUnit.MILLISECONDS);

    Runnable dr =
        new Runnable() {
          @Override
          public void run() {
            SystemClock.this.destroy();
          }
        };

    Runtime.getRuntime().addShutdownHook(new Thread(dr));

    return s;
  }

  /** The destroy of executor service */
  public void destroy() {
    if (scheduler != null) {
      scheduler.shutdown();
    }
  }

  private long currentTimeMillis() {
    return now.get();
  }

  private static class InstanceHolder {
    public static final SystemClock INSTANCE = new SystemClock(1);
  }
}
