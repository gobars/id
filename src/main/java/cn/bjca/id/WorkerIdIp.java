package cn.bjca.id;

import java.net.Inet4Address;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/** 基于当前机器IPv4最后8bit的workerId */
@Slf4j
public class WorkerIdIp implements WorkerId {
  /**
   * 生成worker ID
   *
   * @return worker ID.
   */
  @Override
  public int workerId() {
    return WORKER_ID;
  }

  private static final int WORKER_ID = generate();

  private static int generate() {
    try {
      val addr = Ip.getLocalHostLANAddress();
      if (addr instanceof Inet4Address) {
        return addr.getAddress()[3] & 0xff;
      }

      log.warn("failed to determine LAN IPv4 address, only {} found ", addr.getHostAddress());
    } catch (Exception ex) {
      log.warn("failed to determine LAN address", ex);
    }

    return 0;
  }
}
