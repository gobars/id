package com.github.gobars.id.db;

import com.github.gobars.id.WorkerId;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.util.Pid;
import com.github.gobars.id.worker.WorkerIdHostname;
import com.github.gobars.id.worker.WorkerIdIp;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

/**
 * 从数据库中获得WorkerID
 *
 * @author bingoobjca
 */
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
    return r.insert(s, WorkerIdIp.LOCAL_IP, WorkerIdHostname.HOSTNAME, Pid.PROCESS_ID, reason, biz);
  }
}
