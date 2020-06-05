package com.github.gobars.id;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

public class WorkerIdDbTest {
  @Test
  @SneakyThrows
  public void test1() {
    Class.forName("com.mysql.jdbc.Driver");

    val url = "jdbc:mysql://localhost:3306/id?useSSL=false";
    int id =
        new WorkerIdDb()
            .dataSource(
                new WorkerIdDb.DataSource() {
                  @Override
                  public Connection getConnection() throws SQLException {
                    return DriverManager.getConnection(url, "root", "root");
                  }
                })
            .biz("default")
            .reason("test")
            .workerId();
    assertTrue(id > 0);
  }
}
