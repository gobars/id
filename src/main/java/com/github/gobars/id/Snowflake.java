package com.github.gobars.id;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.util.SystemClock;
import com.github.gobars.id.util.Util;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;

/**
 * 雪花算法实现
 *
 * <p>添加时间回拨处理
 */
@Slf4j
public class Snowflake implements IdNext {
  private final Conf conf;
  /** 保留backwardId和lastTime */
  private final Map<Long, Long> backwardIdLastTimes;

  protected long workerId;
  protected long lastTs;
  protected long sequence;
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
        cur = lastTs + 1;
      }
    } else {
      sequence = 0L;
    }

    lastTs = cur;
    if (backwardIdLastTimes != null) {
      backwardIdLastTimes.put(backwardId, lastTs);
    }

    return cur - conf.getEpoch() << conf.getTimestampShift() & conf.getMaxTimestamp()
        | backwardId << conf.getBackwardShift()
        | workerId << conf.getWorkerIdShift()
        | sequence;
  }

  private long timeBackDeal() {
    long cur = currentTimeMillis() / conf.getRoundMs();
    if (cur >= lastTs) {
      return cur;
    }

    long diff = (lastTs - cur) * conf.getRoundMs();
    // 时间回拨 小于 x 秒时，直接等待diff秒后重新获取时间
    if (diff <= conf.getMaxBackwardSleepMs()) {
      Util.sleep(diff + 10);
      cur = currentTimeMillis() / conf.getRoundMs();
    }

    // 依然落后上次时间
    if (cur < lastTs) {
      rotateBackwardId(cur);
    }

    return cur;
  }

  protected void rotateBackwardId(long cur) {
    if (backwardIdLastTimes != null) {
      for (val e : backwardIdLastTimes.entrySet()) {
        if (e.getValue() <= cur && e.getKey() != this.backwardId) {
          log.info("rotate backward Id from {} to {}", this.backwardId, e.getKey());
          this.backwardId = e.getKey();
          Util.saveBackwardId(this.workerId, this.backwardId);
          return;
        }
      }
    }

    // 如果所有BackwardId都处于时钟回拨, 那么抛出异常
    throw new IllegalStateException(
        "Clock is moving backwards, current time is "
            + cur * conf.getRoundMs()
            + " mills, workerId map = "
            + backwardIdLastTimes);
  }

  protected long currentTimeMillis() {
    switch (conf.getTimestampBy()) {
      case SYSTEM:
        return System.currentTimeMillis();
      case NANO:
        return System.nanoTime();
      default:
        return SystemClock.now();
    }
  }
}
