package com.github.gobars.id;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * 雪花算法实现
 *
 * <p>添加时间回拨处理
 */
@Slf4j
public class Snowflake implements Next {
  private final Conf conf;
  private final long workerId;
  /** 保留backwardId和lastTime */
  private final Map<Long, Long> backwardIdLastTimes;

  @Getter protected long lastMillis;
  @Getter @Setter protected long sequence;
  private long backwardId;

  public Snowflake(Conf conf, long workerId) {
    this.conf = conf;
    this.workerId = workerId & conf.getMaxWorkerId();
    this.backwardId = readBackwardId(workerId);
    this.backwardIdLastTimes = new HashMap<Long, Long>((int) conf.getMaxBackwardId());

    for (long i = 0; i <= conf.getMaxBackwardId(); i++) {
      this.backwardIdLastTimes.put(i, 0L);
    }

    log.debug("IdGenerator created for config {}, workerId {}", conf, workerId);
  }

  static long readBackwardId(long workerId) {
    String file = workerBackwardIdFile(workerId);
    try {
      return Long.parseLong(Files.readFile(file));
    } catch (FileNotFoundException e) {
      // ignore
    } catch (Exception e) {
      log.warn("failed to read {}", file, e);
    }

    return 0L;
  }

  static void saveBackwardId(long workerId, long backwardId) {
    Files.saveFile(workerBackwardIdFile(workerId), String.valueOf(backwardId));
  }

  static String workerBackwardIdFile(long workerId) {
    return Files.homeFile(".worker.backwardId." + workerId);
  }

  @Override
  public synchronized long next() {
    long currentMillis = timeBackDeal();

    if (lastMillis == currentMillis) {
      sequence = ++sequence & conf.getMaxSequence();
      if (sequence == 0L) {
        currentMillis = tilNextMillis(lastMillis);
      }
    } else {
      sequence = 0L;
    }

    lastMillis = currentMillis;
    backwardIdLastTimes.put(backwardId, lastMillis);

    long diff = currentMillis - conf.getEpoch();

    return (diff << conf.getTimestampShift())
        | (backwardId << conf.getBackwardShift())
        | (workerId << conf.getWorkerIdShift())
        | sequence;
  }

  private long timeBackDeal() {
    long currentMillis = currentTimeMillis();
    if (currentMillis >= lastMillis) {
      return currentMillis;
    }

    long diff = lastMillis - currentMillis;
    // 时间回拨 小于 x 秒时，直接等待diff秒后重新获取时间
    if (diff <= conf.getMaxBackwardMillis()) {
      sleep(diff);
      currentMillis = currentTimeMillis();
    }

    // 依然落后上次时间
    if (currentMillis < lastMillis) {
      rotateBackwardId(currentMillis);
    }

    return currentMillis;
  }

  private void rotateBackwardId(long currentMillis) {
    for (val e : backwardIdLastTimes.entrySet()) {
      if (e.getValue() <= currentMillis && e.getKey() != this.backwardId) {
        this.backwardId = e.getKey();
        saveBackwardId(this.workerId, this.backwardId);
        return;
      }
    }

    // 如果所有BackwardId都处于时钟回拨, 那么抛出异常
    throw new IllegalStateException(
        "Clock is moving backwards, current time is "
            + currentMillis
            + " milliseconds, workerId map = "
            + backwardIdLastTimes);
  }

  void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      // ignore
    }
  }

  protected long tilNextMillis(long lastMillis) {
    long millis = currentTimeMillis();
    while (millis <= lastMillis) {
      millis = currentTimeMillis();
    }

    return millis;
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  /** 雪花算法的配置 */
  @Value
  public static class Conf {
    /** 服务器第一次上线时间点, 设置后不允许修改 */
    long epoch;
    /** 时间回拨序号占用比特位数 * */
    int backwardBits;
    /** worker占用比特位数 */
    int workerBits;
    /** 自增序号占用比特位数 */
    int sequenceBits;

    /** 最大自增序号 */
    long maxSequence;
    /** 最大workerID */
    long maxWorkerId;
    /** 最大时间回拨ID */
    long maxBackwardId;

    /** 最大时间回拨 */
    long maxBackwardMillis;

    int workerIdShift;
    int backwardShift;
    int timestampShift;

    public Conf() {
      // 1591173022000L is 2020-06-03 16:30:22
      this(1591173022000L, 2, 8, 12, 1000);
    }

    public Conf(
        long epoch, int backwardBits, int workerBits, int sequenceBits, long maxBackwardMillis) {
      this.epoch = epoch;
      this.backwardBits = backwardBits;
      this.workerBits = workerBits;
      this.sequenceBits = sequenceBits;

      this.maxSequence = ~(-1L << sequenceBits);
      this.maxWorkerId = ~(-1L << workerBits);
      this.maxBackwardId = ~(-1L << backwardBits);

      this.maxBackwardMillis = maxBackwardMillis;

      this.workerIdShift = sequenceBits;
      this.backwardShift = workerBits + sequenceBits;
      this.timestampShift = backwardBits + this.backwardShift;
    }
  }
}
