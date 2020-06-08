package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据hostname命名归来了获取workerId.
 *
 * <p>根据机器名最后的数字编号获取工作进程Id.如果线上机器命名有统一规范,建议使用此种方式.
 *
 * <p>例如机器的HostName为:xx-db-yy-dev-01(公司名-部门名-服务名-环境名-编号),会截取HostName最后的编号01作为workerId.
 */
@Slf4j
public class WorkerIdHostname implements WorkerId {
  public static final String HOSTNAME = getHostname();
  private static final Pattern HOSTNAME_WORKER_ID_PATTERN = Pattern.compile("\\d+$");
  private static final int WORKER_ID = parseWorkerId(HOSTNAME);

  public static int parseWorkerId(String hostname) {
    Matcher m = HOSTNAME_WORKER_ID_PATTERN.matcher(hostname);
    if (m.find()) {
      return Integer.parseInt(m.group());
    }

    return 0;
  }

  public static String getHostname() {
    // IS_OS_WINDOWS ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME")
    // https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/SystemUtils.java
    String host = System.getenv("COMPUTERNAME");
    if (host != null) {
      log.info("got hostname {} from env COMPUTERNAME", host);
      return host;
    }

    host = System.getenv("HOSTNAME");
    if (host != null) {
      log.info("got hostname {} from env HOSTNAME", host);
      return host;
    }

    try {
      InputStream is = Runtime.getRuntime().exec("hostname").getInputStream();
      String s = new BufferedReader(new InputStreamReader(is)).readLine();
      log.info("got hostname {} from exec hostname", s);
      return s;
    } catch (Exception ex) {
      log.warn("exec hostname", ex);
    }

    return "Unknown";
  }

  @Override
  public int workerId() {
    return WORKER_ID;
  }
}
