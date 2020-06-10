package com.github.gobars.id.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.val;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DbTypeTest {
  @Test
  public void detectDbTypeMySQL() {
    val dataSource = new DruidDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl(
        "jdbc:mysql://localhost:3306/id?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=yes&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
    dataSource.setUsername("root");
    dataSource.setPassword("root");

    assertThat(DbType.getDbType(dataSource)).isEqualTo(DbType.MYSQL);
  }

  @Test
  public void detectDbTypeOracle() {
    val dataSource = new DruidDataSource();
    dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
    dataSource.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
    dataSource.setUsername("system");
    dataSource.setPassword("oracle");

    assertThat(DbType.getDbType(dataSource)).isEqualTo(DbType.ORACLE);
  }
}
