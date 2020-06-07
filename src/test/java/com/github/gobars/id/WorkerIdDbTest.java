package com.github.gobars.id;

import static org.junit.Assert.assertTrue;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

public class WorkerIdDbTest {
  @Test
  @SneakyThrows
  public void test1() {
    Class.forName("com.mysql.jdbc.Driver");

    val url = "jdbc:mysql://localhost:3306/id?useSSL=false";
    val ds = new ConnGetter.JdbcConnGetter(url, "root", "root");

    int id = new WorkerIdDb().connGetter(ds).biz("default").reason("test").workerId();
    assertTrue(id > 0);
  }
}
