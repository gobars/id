package com.github.gobars.id;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * ID生成器入口类。
 *
 * @author bingoohuang
 */
@UtilityClass
public class DbId {
  private SnowflakeDb next;

  static {
    configure("jdbc:mysql://localhost:3306/id?useSSL=false", "root", "root");
  }

  public void configure(final DataSource ds) {
    val wds =
        new WorkerIdDb.DataSource() {
          @Override
          public Connection getConnection() throws SQLException {
            return ds.getConnection();
          }
        };
    configure(new WorkerIdDb().dataSource(wds).biz("default"));
  }

  public void configure(final String url, final String user, final String password) {
    val ds =
        new WorkerIdDb.DataSource() {
          @Override
          public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
          }
        };
    configure(new WorkerIdDb().dataSource(ds).biz("default"));
  }

  public void configure(WorkerIdDb workerIdDb) {
    next = new SnowflakeDb(new Snowflake.Conf(Snowflake.fromSpec(Id.SPEC)), workerIdDb);
  }

  /**
   * 获得下一个ID.
   *
   * @return 下一个ID.
   */
  public long next() {
    return next.next();
  }
}
