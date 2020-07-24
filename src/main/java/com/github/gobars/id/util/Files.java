package com.github.gobars.id.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.nio.channels.OverlappingFileLockException;

@UtilityClass
@Slf4j
public class Files {
  public final String GOBARS_ID = System.getProperty("user.home") + File.separator + ".gobars_id";

  static {
    File dir = new File(GOBARS_ID);
    dir.mkdirs();
    if (!dir.exists()) {
      throw new RuntimeException("create dirs ~/.gobarsid failed");
    }
  }

  public int findAvailableWorkerID(int workerID) {
    if (lockWorkerID(workerID)) {
      return workerID;
    }

    for (int i = workerID + 100, j = 0; j < 1000; i++) {
      if (lockWorkerID(i)) {
        return i;
      }
    }

    return -1;
  }

  public boolean lockWorkerID(int workerID) {
    return tryLockFile(GOBARS_ID + File.separator + "workerID." + workerID);
  }

  @SneakyThrows
  public boolean tryLockFile(String filename) {
    try {
      return new RandomAccessFile(filename, "rw").getChannel().tryLock() != null;
    } catch (OverlappingFileLockException e) {
      return false;
    }
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

  public String homeFile(String filename) {
    return System.getProperty("user.home") + File.separator + filename;
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
