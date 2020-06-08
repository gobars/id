package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;
import com.github.gobars.id.util.Ip;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * 基于当前机器IPv4最后8bit的workerId.
 *
 * <p>根据机器IP获取工作进程Id,如果线上机器的IP二进制表示的最后8位不重复,建议使用此种方式,
 *
 * <p>列如机器的IP为192.168.1.108 ,设置workerId为108. 更多见： https://www.jianshu.com/p/7f0661ddd6dd
 */
@Slf4j
public class WorkerIdIp implements WorkerId {
  public static String localIP;
  private static int workerId;

  static {
    try {
      val addr = Ip.getLocalHostLANAddress();
      localIP = addr.getHostAddress();
      byte[] bytes = addr.getAddress();
      workerId = bytes[bytes.length - 1] & 0xff;
    } catch (Exception ex) {
      log.warn("failed to determine LAN address", ex);
    }
  }

  /**
   * 生成worker ID
   *
   * @return worker ID.
   */
  @Override
  public int workerId() {
    return workerId;
  }
}
