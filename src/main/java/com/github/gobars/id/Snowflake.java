package com.github.gobars.id;

import java.util.HashMap;
import java.util.Map;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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

  @Getter protected long lastTs;
  @Getter @Setter protected long sequence;
  private long backwardId;

  public Snowflake(Conf conf, long workerId) {
    this.conf = conf;
    this.workerId = workerId & conf.getMaxWorkerId();
    this.backwardId = Util.readBackwardId(workerId);
    this.backwardIdLastTimes = conf.initBackwardIdLastTimesMap();

    log.debug("Snowflake created from conf {} with workerId {}", conf, workerId);
  }

  @Override
  public synchronized long next() {
    long cur = timeBackDeal();

    if (lastTs == cur) {
      sequence = ++sequence & conf.getMaxSequence();
      if (sequence == 0L) {
        cur = tilNext(lastTs);
      }
    } else {
      sequence = 0L;
    }

    lastTs = cur;
    backwardIdLastTimes.put(backwardId, lastTs);

    return cur - conf.getEpoch() << conf.getTimestampShift() & conf.getMaxTimestamp()
        | backwardId << conf.getBackwardShift()
        | workerId << conf.getWorkerIdShift()
        | sequence;
  }

  private long timeBackDeal() {
    long cur = currentTimeMillis() / conf.getRoundMillis();
    if (cur >= lastTs) {
      return cur;
    }

    long diff = lastTs - cur;
    // 时间回拨 小于 x 秒时，直接等待diff秒后重新获取时间
    if (diff <= conf.getMaxBackwardMillis() / conf.getRoundMillis()) {
      Util.sleep(diff * conf.getRoundMillis());
      cur = currentTimeMillis() / conf.getRoundMillis();
    }

    // 依然落后上次时间
    if (cur < lastTs) {
      rotateBackwardId(cur);
    }

    return cur;
  }

  private void rotateBackwardId(long cur) {
    for (val e : backwardIdLastTimes.entrySet()) {
      if (e.getValue() <= cur && e.getKey() != this.backwardId) {
        this.backwardId = e.getKey();
        Util.saveBackwardId(this.workerId, this.backwardId);
        return;
      }
    }

    // 如果所有BackwardId都处于时钟回拨, 那么抛出异常
    throw new IllegalStateException(
        "Clock is moving backwards, current time is "
            + cur * conf.getRoundMillis()
            + " mills, workerId map = "
            + backwardIdLastTimes);
  }

  protected long tilNext(long last) {
    long cur = currentTimeMillis() / conf.getRoundMillis();
    while (cur <= last) {
      Util.sleep(conf.getRoundMillis());
      cur = currentTimeMillis() / conf.getRoundMillis();
    }

    return cur;
  }

  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  /** 雪花算法的配置 */
  @Value
  @Builder
  public static class BaseConf {
    /** 服务器第一次上线时间点, 设置后不允许修改 */
    long epoch;
    /** 时间戳占用比特位数 */
    int timestampBits;
    /** 规整到的时间单位 */
    int roundMillis;
    /** 时间回拨序号占用比特位数 */
    int backwardBits;
    /** worker占用比特位数 */
    int workerBits;
    /** 自增序号占用比特位数 */
    int sequenceBits;
    /** 最大时间回拨 */
    long maxBackwardMillis;
  }

  @Value
  public static class Conf {
    /** 服务器第一次上线时间点, 设置后不允许修改 */
    long epoch;
    /** 时间戳占用比特位数 */
    int timestampBits;
    /** 规整到的时间单位 */
    int roundMillis;
    /** 时间回拨序号占用比特位数 */
    int backwardBits;
    /** worker占用比特位数 */
    int workerBits;
    /** 自增序号占用比特位数 */
    int sequenceBits;
    /** 最大时间回拨 */
    long maxBackwardMillis;

    /** 最大自增序号 */
    long maxSequence;
    /** 最大workerID */
    long maxWorkerId;
    /** 最大时间回拨ID */
    long maxBackwardId;

    int workerIdShift;
    int backwardShift;
    int timestampShift;
    long maxTimestamp;

    public Conf(BaseConf baseConf) {
      this.epoch = baseConf.epoch;
      this.timestampBits = baseConf.timestampBits;
      this.roundMillis = baseConf.roundMillis;
      this.backwardBits = baseConf.backwardBits;
      this.workerBits = baseConf.workerBits;
      this.sequenceBits = baseConf.sequenceBits;
      this.maxBackwardMillis = baseConf.maxBackwardMillis;

      this.maxSequence = ~(-1L << sequenceBits);
      this.maxWorkerId = ~(-1L << workerBits);
      this.maxBackwardId = ~(-1L << backwardBits);
      this.maxTimestamp = ~(-1L << timestampBits);

      this.workerIdShift = sequenceBits;
      this.backwardShift = workerBits + sequenceBits;
      this.timestampShift = backwardBits + this.backwardShift;
    }

    public Map<Long, Long> initBackwardIdLastTimesMap() {
      val m = new HashMap<Long, Long>((int) this.maxBackwardId);

      for (long i = 0; i <= this.maxBackwardId; i++) {
        m.put(i, 0L);
      }

      return m;
    }
  }
}
