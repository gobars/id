package com.github.gobars.id;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.worker.*;
import lombok.experimental.UtilityClass;

/**
 * ID生成器入口类。
 *
 * @author bingoohuang
 */
@UtilityClass
public class Id {
  public final String SPEC =
      "epoch=20200603,timestampBits=41,backwardBits=2,workerBits=8,seqBits=12,roundMs=1,maxBackwardSleepMs=1000";

  private final IdNext next;

  static {
    next =
        new Snowflake(
            Conf.fromSpec(SPEC),
            new WorkerIdComposite(
                    new WorkerIdEnv(),
                    new WorkerIdLocalLockFile(),
                    new WorkerIdHostname(),
                    new WorkerIdIp(),
                    new WorkerIdRandom())
                .workerId());
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
