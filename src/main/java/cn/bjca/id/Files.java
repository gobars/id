package cn.bjca.id;

import java.io.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class Files {

  public String readFile(String filePath) throws IOException {
    DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
    try {
      long len = new File(filePath).length();
      if (len > Integer.MAX_VALUE) {
        throw new IOException("File " + filePath + " too large, was " + len + " bytes.");
      }
      byte[] bytes = new byte[(int) len];
      dis.readFully(bytes);
      return new String(bytes, "UTF-8");
    } finally {
      closeQuietly(dis);
    }
  }

  public void closeQuietly(Closeable out) {
    if (out == null) {
      return;
    }

    try {
      out.close();
    } catch (IOException ex) {
      // ignore
    }
  }

  public String homeFile(String filename) {
    return System.getProperty("user.home") + File.separator + filename;
  }

  public void saveFile(String filename, String value) {
    PrintWriter out = null;

    try {
      out = new PrintWriter(filename);
      out.print(value);
    } catch (Exception e) {
      log.warn("failed to write {}", filename, e);
    } finally {
      closeQuietly(out);
    }
  }
}
