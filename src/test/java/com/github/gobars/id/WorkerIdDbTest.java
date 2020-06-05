package com.github.gobars.id;

import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

public class WorkerIdDbTest {
  @Test
  @SneakyThrows
  public void test1() {
    Class.forName("com.mysql.jdbc.Driver");
    @Cleanup
    val conn =
        DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/id?useSSL=false&serverTimezone=UTC", "root", "root");

    int id = new WorkerIdDb(conn, "xxx").workerId();
    assertTrue(id > 0);
  }
}
