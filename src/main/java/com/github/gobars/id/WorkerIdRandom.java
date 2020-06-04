package com.github.gobars.id;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

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
