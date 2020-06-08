package com.github.gobars.id.conf;

import java.text.SimpleDateFormat;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/** 雪花算法的基本配置 */
@Value
@Builder
@Slf4j
public class Base {
  /**
   * 1 服务器第一次上线时间点, 设置后不允许修改
   *
   * <p>默认值: 2020-06-03的毫秒数1591113600000L
   */
  @Builder.Default long epoch = 1591113600000L;
  /** 2 时间戳占用比特位数 */
  @Builder.Default int timestampBits = 41;
  /** 3 规整到的时间单位 */
  @Builder.Default int roundMs = 1;
  /** 4 时间回拨序号占用比特位数 */
  @Builder.Default int backwardBits = 0;
  /** 5 worker占用比特位数 */
  @Builder.Default int workerBits = 10;
  /** 6 自增序号占用比特位数 */
  @Builder.Default int seqBits = 12;
  /** 7 最大时间回拨 */
  @Builder.Default long maxBackwardSleepMs = 1000;

  @SneakyThrows
  public static Base fromSpec(String spec) {
    val builder = Base.builder();

    String[] items = spec.split(",");
    for (String item : items) {
      item = item.trim();
      if (item.length() == 0) {
        continue;
      }

      String[] parts = item.split("=", 2);
      String key = parts[0];
      String val = parts[1];
      if (key.equals("epoch")) {
        long epoch = new SimpleDateFormat("yyyyMMdd").parse(val).getTime();
        builder.epoch(epoch);
      } else if (key.equals("timestampBits")) {
        builder.timestampBits(Integer.parseInt(val));
      } else if (key.equals("roundMs")) {
        builder.roundMs(Integer.parseInt(val));
      } else if (key.equals("backwardBits")) {
        builder.backwardBits(Integer.parseInt(val));
      } else if (key.equals("workerBits")) {
        builder.workerBits(Integer.parseInt(val));
      } else if (key.equals("seqBits")) {
        builder.seqBits(Integer.parseInt(val));
      } else if (key.equals("maxBackwardSleepMs")) {
        builder.maxBackwardSleepMs(Integer.parseInt(val));
      }
    }

    Base base = builder.build();

    log.info("base built {} from spec {}", base, spec);

    return base;
  }
}
