package com.github.gobars.id.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Bench {
  @Benchmark
  public long nano() {
    // System.nanoTime() returns the number of nanoseconds
    // since some arbitrary point in the past.
    return System.nanoTime();
  }

  @Benchmark
  public long millis() {
    // https://dzone.com/articles/java-advent-calendar-measuring-time-from-java-to-k
    // System.currentTimeMillis() returns the number of milliseconds
    // since the start of the Unix epoch - January 1, 1970, UTC.

    // The finest possible granularity of currentTimeMillis() is 1 millisecond.
    // It makes measuring anything shorter than 1ms impossible.
    // The fact that currentTimeMillis() uses January 1, 1970, UTC
    // as a reference point is both good and bad.

    // Why is it good? We can compare currentTimeMillis() values
    // returned by two different JVMs and even by two different computers.

    // Why is it bad? The comparison won't be very useful when our computers don't have synchronized
    // time. The clocks in typical server farms aren't perfectly synchronized and there will always
    // be some gap.

    // This can still be acceptable if I'm comparing log files from two different systems: it's OK
    // if timestamps aren't perfectly synchronized. However, sometimes the gap can lead to
    // disastrous results, for example when it's used for conflict resolution in distributed
    // systems.

    // The other problem is that the returned values are not guaranteed to be monotonically
    // increasing. What does it mean? When you have two consecutive calls of currentTimeMillis(),
    // the second call can return a lower value than the first one. This is counterintuitive and can
    // lead to nonsensical results such as elapsed time being a negative number. It's clear that
    // currentTimeMillis() is not a good choice to measure the elapsed time inside an application.
    // What about nanoTime()?

    return System.currentTimeMillis();
  }
}
