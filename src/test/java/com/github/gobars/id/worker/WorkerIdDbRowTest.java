package com.github.gobars.id.worker;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.gobars.id.Id;
import com.github.gobars.id.IdNext;
import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.SnowflakeDbRow;
import com.github.gobars.id.db.WorkerIdDbRow;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkerIdDbRowTest {
  @Test
  @SneakyThrows
  public void mysql() {
    Class.forName("com.mysql.jdbc.Driver");

    val url = "jdbc:mysql://localhost:3306/id?useSSL=false";
    val ds = new ConnGetter.JdbcConnGetter(url, "root", "root");

    IdNext IdNext = new SnowflakeDbRow(Conf.fromSpec(Id.SPEC), new WorkerIdDbRow().connGetter(ds));
    System.out.println(IdNext.next());

    for (int i = 0; i < 10; i++) {
      int workerID = new WorkerIdDbRow().connGetter(ds).table("t_worker_id").workerId();
      System.out.println(workerID);
      System.out.println(IdNext.next());

      assertTrue(workerID > 0);
    }
  }

  @Test
  public void oracle() {
    val dds = new DruidDataSource();
    dds.setDriverClassName("oracle.jdbc.OracleDriver");
    dds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
    dds.setUsername("system");
    dds.setPassword("oracle");

    val ds = new ConnGetter.DsConnGetter(dds);

    for (int i = 0; i < 10; i++) {
      int id = new WorkerIdDbRow().connGetter(ds).table("t_worker_id").workerId();
      assertTrue(id > 0);
    }
  }
}
