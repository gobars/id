package com.github.gobars.id;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.worker.*;
import com.github.ksuid.Ksuid;
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

  /**
   * 获得下一个 KsUID.
   * Roughly sortable by creation time 按时间排序。
   * Can be stored as a string of 27 chars; 固定27个字符。
   * Can be stored as an array of 20 bytes; 20个字节。
   * String format is encoded to base-62 (0-9A-Za-z); base62 编码。
   * String format is URL safe and has no hyphens. URL 安全。
   *
   * @return 下一个 KsUID.
   */
  public String ksuid() {
    return Ksuid.newKsuid().toString();
  }
}
