package com.github.gobars.id.db;

import com.github.gobars.id.Id;
import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.worker.WorkerIdDb;
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
    configure(new WorkerIdDb().connGetter(new ConnGetter.DsConnGetter(ds)).biz("default"));
  }

  public void configure(final String url, final String user, final String password) {
    val ds = new ConnGetter.JdbcConnGetter(url, user, password);
    configure(new WorkerIdDb().connGetter(ds).biz("default"));
  }

  public void configure(WorkerIdDb workerIdDb) {
    next = new SnowflakeDb(Conf.fromSpec(Id.SPEC), workerIdDb);
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
