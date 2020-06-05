package com.github.gobars.id;

import lombok.experimental.UtilityClass;

/**
 * ID生成器入口类。
 *
 * @author bingoohuang
 */
@UtilityClass
public class Id {
  public final String SPEC =
      "epoch=2020-06-03 16:30:22,timestampBits=41,backwardBits=2,workerBits=8,sequenceBits=12,roundMillis=1,maxBackwardMillis=1000";

  private final Snowflake next =
      new Snowflake(
          new Snowflake.Conf(Snowflake.fromSpec(SPEC)),
          new WorkerIdComposite(
                  new WorkerIdEnv(), new WorkerIdHostname(), new WorkerIdIp(), new WorkerIdRandom())
              .workerId());

  /**
   * 获得下一个ID.
   *
   * @return 下一个ID.
   */
  public long next() {
    return next.next();
  }
}
