package com.github.gobars.id.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import javax.sql.DataSource;

public enum DbType {
  /** 当前连接的是Oracle库 */
  ORACLE,
  /** 当前连接的是MySQL库 */
  MYSQL,
  /** 未知 */
  UNKNOWN;

  @SneakyThrows
  public static DbType getDbType(DataSource dataSource) {
    @Cleanup val conn = dataSource.getConnection();
    return getDbType(conn);
  }

  @SneakyThrows
  public static DbType getDbType(java.sql.Connection conn) {
    val metaData = conn.getMetaData();
    val driverName = metaData.getDriverName().toUpperCase();
    if (driverName.contains("MYSQL")) {
      return DbType.MYSQL;
    } else if (driverName.contains("ORACLE")) {
      return DbType.ORACLE;
    }

    return DbType.UNKNOWN;
  }
}
