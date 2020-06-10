package com.github.gobars.id.worker;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.WorkerIdDb;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkerIdDbTest {
  @Test
  @SneakyThrows
  public void mysql() {
    Class.forName("com.mysql.jdbc.Driver");

    val url = "jdbc:mysql://localhost:3306/id?useSSL=false";
    val ds = new ConnGetter.JdbcConnGetter(url, "root", "root");

    for (int i = 0; i < 10; i++) {
      int id = new WorkerIdDb().connGetter(ds).biz("default").reason("test").workerId();
      assertTrue(id > 0);
    }
  }

  @Test
  public void oracle() {
    val dataSource = new DruidDataSource();
    dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
    dataSource.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
    dataSource.setUsername("system");
    dataSource.setPassword("oracle");

    val ds = new ConnGetter.DsConnGetter(dataSource);

    for (int i = 0; i < 10; i++) {
      int id = new WorkerIdDb().connGetter(ds).biz("default").reason("test").workerId();
      assertTrue(id > 0);
    }
  }
}
