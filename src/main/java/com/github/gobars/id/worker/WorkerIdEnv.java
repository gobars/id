package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;

/**
 * 获得从环境变量中指定的WorkerID
 *
 * @author bingoohuang
 */
public class WorkerIdEnv implements WorkerId {
  public static final String WORKER_ID = "WORKERID";

  @Override
  public int workerId() {
    String v = System.getProperty(WORKER_ID);
    if (isValid(v)) {
      return Integer.parseInt(v);
    }

    v = System.getenv(WORKER_ID);
    if (isValid(v)) {
      return Integer.parseInt(v);
    }

    return 0;
  }

  private boolean isValid(String v) {
    return v != null && v.matches("^\\d+$");
  }
}
