package com.github.gobars.id.worker;

import com.github.gobars.id.WorkerId;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.SqlRunner;
import com.github.gobars.id.util.Pid;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

@Data
@Accessors(fluent = true)
public class WorkerIdDb implements WorkerId {
  private ConnGetter connGetter;
  private String table = "worker_id";
  private String biz;
  private String reason;

  @Override
  @SneakyThrows
  public int workerId() {
    val s = "insert into " + table + "(ip, hostname, pid, reason, biz) values (?, ?, ?, ?, ?)";
    @Cleanup val conn = connGetter.getConn();
    val r = new SqlRunner(conn);
    return r.insert(s, WorkerIdIp.localIP, WorkerIdHostname.HOSTNAME, Pid.pid, reason, biz);
  }
}
