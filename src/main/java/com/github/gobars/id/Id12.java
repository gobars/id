package com.github.gobars.id;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Id12 {
  public final String SPEC =
      "epoch=2020-06-03 16:30:22,timestampBits=29,backwardBits=1,workerBits=3,sequenceBits=6,roundMillis=1000,maxBackwardMillis=1000";

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
