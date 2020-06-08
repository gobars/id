package com.github.gobars.id.util;

import java.io.*;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@UtilityClass
@Slf4j
public class Files {

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
