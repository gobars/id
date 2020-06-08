package com.github.gobars.id.ext;

import com.github.gobars.id.IdNext;
import com.github.gobars.id.Snowflake;
import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.worker.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Id12 {
  public final String SPEC =
      "epoch=20200603,timestampBits=29,backwardBits=1,workerBits=3,seqBits=6,roundMs=1000,maxBackwardSleepMs=1000";

  private final IdNext next =
      new Snowflake(
          Conf.fromSpec(SPEC),
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
