package com.github.gobars.id.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.FileNotFoundException;
import java.util.Scanner;

@Slf4j
@UtilityClass
public class Util {
  public void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      // ignore
    }
  }

  public void saveBackwardId(long workerId, long backwardId) {
    Files.saveFile(backwardIdFile(workerId), String.valueOf(backwardId));
  }

  public long readBackwardId(long workerId) {
    String file = backwardIdFile(workerId);
    try {
      return Long.parseLong(Files.readFile(file));
    } catch (FileNotFoundException e) {
      // ignore
    } catch (Exception e) {
      log.warn("failed to read {}", file, e);
    }

    return 0L;
  }

  public String backwardIdFile(long workerId) {
    return Files.homeFile(".worker.backwardId." + workerId);
  }

  @SneakyThrows
  public String exec(String execCommand) {
    val proc = Runtime.getRuntime().exec(execCommand);
    @Cleanup val stream = proc.getInputStream();
    @Cleanup val scanner = new Scanner(stream).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }
}
