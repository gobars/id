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
  DM,
  /**金仓*/
  KINGBASE,
  POSTGRESQL,
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
    } else if (driverName.contains("KINGBASE")){
      return DbType.KINGBASE;
    } else if (driverName.contains("DMDRIVER")){
      return DbType.DM;
    }

    return DbType.UNKNOWN;
  }
}
