package com.github.gobars.id;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

@Data
@Accessors(fluent = true)
public class WorkerIdDb implements WorkerId {
  private DataSource dataSource;
  private String tableName = "worker_id";
  private String biz;
  private String reason;

  @Override
  @SneakyThrows
  public int workerId() {
    val sql = "insert into " + tableName + "(hostname, reason, biz) values (?, ?, ?)";
    @Cleanup val conn = dataSource.getConnection();
    return new SqlRunner(conn).insert(sql, WorkerIdHostname.HOSTNAME, reason, biz);
  }

  public interface DataSource {
    /**
     * Attempts to establish a connection with the data source that this {@code DataSource} object
     * represents.
     *
     * @return a connection to the data source
     * @exception SQLException if a database access error occurs
     * @throws java.sql.SQLTimeoutException when the driver has determined that the timeout value
     *     specified by the {@code setLoginTimeout} method has been exceeded and has at least tried
     *     to cancel the current database connection attempt
     */
    Connection getConnection() throws SQLException;
  }
}
