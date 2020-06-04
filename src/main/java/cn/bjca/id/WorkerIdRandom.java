package cn.bjca.id;

import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;

/** 随机生成workerId. */
@Slf4j
public class WorkerIdRandom implements WorkerId {
  private static final int WORKER_ID = generate();

  private static int generate() {
    int v = new SecureRandom().nextInt();
    if (v == 0) {
      return 1;
    }

    if (v < 0) {
      return -v;
    }

    return v;
  }

  @Override
  public int workerId() {
    return WORKER_ID;
  }
}
