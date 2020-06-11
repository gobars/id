package com.github.gobars.id.conf;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Value
public class Conf {
  /** 服务器第一次上线时间点, 设置后不允许修改 */
  long epoch;
  /** 时间戳占用比特位数 */
  int timestampBits;
  /** 规整到的时间单位 */
  int roundMs;
  /** 时间回拨序号占用比特位数 */
  int backwardBits;
  /** worker占用比特位数 */
  int workerBits;
  /** 自增序号占用比特位数 */
  int seqBits;
  /** 最大时间回拨 */
  long maxBackwardSleepMs;
  /** 8 时间戳计算 */
  TimestampByEnum timestampBy;

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

  public Conf(Base base) {
    this.epoch = base.getEpoch();
    this.timestampBits = base.getTimestampBits();
    this.roundMs = base.getRoundMs();
    this.backwardBits = base.getBackwardBits();
    this.workerBits = base.getWorkerBits();
    this.seqBits = base.getSeqBits();
    this.maxBackwardSleepMs = base.getMaxBackwardSleepMs();
    this.timestampBy = parseTimestampBy(base.getTimestampBy());

    this.maxSequence = ~(-1L << seqBits);
    this.maxWorkerId = ~(-1L << workerBits);
    this.maxBackwardId = ~(-1L << backwardBits);
    this.maxTimestamp = ~(-1L << timestampBits);

    this.workerIdShift = seqBits;
    this.backwardShift = workerBits + seqBits;
    this.timestampShift = backwardBits + this.backwardShift;
  }

  public enum TimestampByEnum {
    /** 基于SystemClock.now() */
    NANO,
    /** 基于System.currentMillis() */
    SYSTEM,
    /** SystemClock.now() */
    CACHE;
  }

  private TimestampByEnum parseTimestampBy(String timestampBy) {
    if ("nano".equals(timestampBy)) {
      return TimestampByEnum.NANO;
    } else if ("system".equals(timestampBy)) {
      return TimestampByEnum.SYSTEM;
    } else {
      // nano and the default
      return TimestampByEnum.CACHE;
    }
  }

  @SneakyThrows
  public static Conf fromSpec(String spec) {
    return new Conf(Base.fromSpec(spec));
  }

  public Map<Long, Long> initBackwardIdLastTimesMap() {
    if (this.maxBackwardId <= 0) {
      return null;
    }

    val m = new HashMap<Long, Long>((int) this.maxBackwardId);

    for (long i = 0; i <= this.maxBackwardId; i++) {
      m.put(i, 0L);
    }

    return m;
  }
}
