package com.github.gobars.id;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class Id12 {
  private final Next next;

  static {
    // 1591173022000L is 2020-06-03 16:30:22
    val conf =
        new Snowflake.Conf(
            Snowflake.BaseConf.builder()
                .epoch(1591173022000L)
                .timestampBits(29)
                .backwardBits(1)
                .workerBits(3)
                .sequenceBits(6)
                .roundMillis(1000)
                .maxBackwardMillis(1000)
                .build());
    val c =
        new WorkerIdComposite(
            new WorkerIdEnv(), new WorkerIdHostname(), new WorkerIdIp(), new WorkerIdRandom());
    next = new Snowflake(conf, c.workerId());
  }

  /**
   * 获得下一个ID.
   *
   * @return 下一个ID.
   */
  public long next() {
    return next.next();
  }
}
