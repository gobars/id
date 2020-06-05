package com.github.gobars.id;

import java.sql.Connection;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

@Value
public class WorkerIdDb implements WorkerId {
  Connection cnn;
  String biz;

  @Override
  @SneakyThrows
  public int workerId() {
    val sql = "insert into worker_id(hostname, biz) values (?, ?)";
    return new SqlRunner(cnn).insert(sql, WorkerIdHostname.HOSTNAME, biz);
  }
}
