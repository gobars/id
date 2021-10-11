package com.github.gobars.id.db;

import com.github.gobars.id.WorkerId;
import com.github.gobars.id.conf.ConnGetter;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

/**
 * 从数据库中的行记录中获得WorkerID
 *
 * @author bingoobjca
 */
@Data
@Accessors(fluent = true)
public class WorkerIdDbRow implements WorkerId {
  private ConnGetter connGetter;
  private String table = "t_worker_id";
  private String name = "default";

  @Override
  @SneakyThrows
  public int workerId() {
    @Cleanup val conn = connGetter.getConn();
    boolean autoCommit = conn.getAutoCommit();
    if (autoCommit) {
      conn.setAutoCommit(false);
    }

    val r = new SqlRunner(conn, false);
    long workerId = 1;
    int effected =
        r.update("update " + table + " set value = value + step + 1 where name = ?", name);
    if (effected <= 0) {
      r.insert("insert into " + table + "(name, value) values(?, 1)", name);
    } else {
      workerId = r.selectLong("select value from " + table + " where name = ?", name);
    }

    conn.commit();
    if (autoCommit) {
      conn.setAutoCommit(true);
    }
    return (int) workerId;
  }
}
