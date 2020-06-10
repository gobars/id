package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;

import java.security.SecureRandom;

/** 随机生成workerId. */
public class WorkerIdRandom implements WorkerId {
  private static final int WORKER_ID = generate();

  static int generate() {
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
