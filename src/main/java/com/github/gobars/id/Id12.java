package com.github.gobars.id;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class Id12 {
  private Next next;

  static {
    // 1591173022000L is 2020-06-03 16:30:22
    val conf = new Snowflake.Conf(1591173022000L, 27, 10000, 2, 3, 7, 1000);
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
