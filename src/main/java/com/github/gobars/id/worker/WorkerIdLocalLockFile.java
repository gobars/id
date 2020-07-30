package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;
import com.github.gobars.id.util.Files;

/**
 * 获得从本地WorkerID锁文件中获得已有的workerID.
 *
 * @author bingoohuang
 */
public class WorkerIdLocalLockFile implements WorkerId {
  @Override
  public int workerId() {
    return Files.tryAvailableLocalWorker(true);
  }
}
