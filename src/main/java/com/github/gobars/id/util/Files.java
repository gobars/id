package com.github.gobars.id.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
@Slf4j
public class Files {
  public final String GOBARS_ID = System.getProperty("user.home") + File.separator + ".gobars_id";
  public static final String WORKER_ID_PREFIX = "workerID.";

  static {
    File dir = new File(GOBARS_ID);
    dir.mkdirs();
    if (!dir.exists()) {
      throw new RuntimeException("create dirs ~/.gobarsid failed");
    }
  }

  public String homeFile(String filename) {
    return GOBARS_ID + File.separator + filename;
  }

  public int findAvailableWorkerID(int workerID, boolean allowOverlapping) {
    if (lockWorkerID(workerID, allowOverlapping)) {
      return workerID;
    }

    int incr = workerID % 100;
    for (int i = workerID + incr, j = 0; j < 1024; i += incr) {
      if (lockWorkerID(i, allowOverlapping)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * 找出目前本地已经生成的锁定文件的一个可用worderID列表.
   *
   * @param allowOverlapping if allow overlapping for the lock.
   * @return 本地锁定文件的workerID列表.
   */
  public int tryAvailableLocalWorker(boolean allowOverlapping) {
    File[] files = new File(GOBARS_ID).listFiles();

    if (files == null) {
      return -1;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        continue;
      }

      String fn = file.getName();
      if (!fn.startsWith(WORKER_ID_PREFIX)) {
        continue;
      }

      String workerId = fn.substring(WORKER_ID_PREFIX.length());
      try {
        int i = Integer.parseInt(workerId);
        if (lockWorkerID(i, allowOverlapping)) {
          return i;
        }
      } catch (Exception ignore) {
        log.warn("bad worker id {}", workerId);
      }
    }

    return -1;
  }

  public boolean lockWorkerID(int workerID, boolean allowOverlapping) {
    return tryLockFile(homeFile(WORKER_ID_PREFIX + workerID), allowOverlapping);
  }

  @SneakyThrows
  public boolean tryLockFile(String filename, boolean allowOverlapping) {
    try {
      val f = new RandomAccessFile(filename, "rw");
      if (f.getChannel().tryLock() == null) {
        return false;
      }

      f.writeBytes(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
      f.writeBytes(" pid=");
      f.writeBytes(processId);
      return true;
    } catch (OverlappingFileLockException e) {
      return allowOverlapping;
    } catch (Exception ignore) {

    }

    return false;
  }

  public final String processId = getProcessId("0");

  private String getProcessId(final String fallback) {
    // Note: may fail in some JVM implementations
    // therefore fallback has to be provided

    // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
    final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
    final int index = jvmName.indexOf('@');

    if (index < 1) {
      // part before '@' empty (index = 0) / '@' not found (index = -1)
      return fallback;
    }

    try {
      return Long.toString(Long.parseLong(jvmName.substring(0, index)));
    } catch (NumberFormatException e) {
      // ignore
    }
    return fallback;
  }

  public String readFile(String filePath) throws IOException {
    @Cleanup val dis = new DataInputStream(new FileInputStream(filePath));
    long len = new File(filePath).length();
    if (len > Integer.MAX_VALUE) {
      throw new IOException("File " + filePath + " too large, was " + len + " bytes.");
    }
    byte[] bytes = new byte[(int) len];
    dis.readFully(bytes);
    return new String(bytes, "UTF-8");
  }

  public void saveFile(String filename, String value) {
    try {
      @Cleanup val out = new PrintWriter(filename);
      out.print(value);
    } catch (Exception e) {
      log.warn("failed to write {}", filename, e);
    }
  }
}
