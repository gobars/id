package com.github.gobars.id;

import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * ID生成器入口类。
 *
 * @author bingoohuang
 */
@UtilityClass
public class Id {
  private Next next;

  static {
    val c =
        new WorkerIdComposite(
            new WorkerIdEnv(), new WorkerIdHostname(), new WorkerIdIp(), new WorkerIdRandom());
    val conf =
        new Snowflake.Conf(
            Snowflake.BaseConf.builder()
                // 1591173022000L is 2020-06-03 16:30:22
                .epoch(1591173022000L)
                .timestampBits(41)
                .backwardBits(2)
                .workerBits(8)
                .sequenceBits(12)
                .roundMillis(1)
                .maxBackwardMillis(1000)
                .build());
    Id.next = new Snowflake(conf, c.workerId());
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
