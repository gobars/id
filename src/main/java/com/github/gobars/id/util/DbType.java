package com.github.gobars.id.util;

import java.sql.SQLException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import javax.sql.DataSource;
import java.sql.Connection;

public enum DbType {
  /** 当前连接的是Oracle库 */
  ORACLE,
  /** 当前连接的是MySQL库 */
  MYSQL,
  /** 达梦 */
  DM,
  /** 金仓 */
  KINGBASE,
  POSTGRESQL,
  /** 南大神通 */
  SHENTONG,
  /** 未知 */
  UNKNOWN;



  @SneakyThrows
  public static DbType getDbType(DataSource dataSource) {
    @Cleanup val conn = dataSource.getConnection();
    return getDbType(conn);
  }

  @SneakyThrows
  public static DbType getDbType(Connection conn) {
    val metaData = conn.getMetaData();
    DbType dbType = getDbTypeByDatabaseProductName(metaData.getDatabaseProductName());
    if (!DbType.UNKNOWN.equals(dbType)) {
      return dbType;
    }
    return getDbTypeByDriverName(metaData.getDriverName());
  }
  public static DbType getDbTypeByDatabaseProductName(String productName) {
    for (DbType type : DbType.values()) {
      if (type.name().equalsIgnoreCase(productName)) {
        return type;
      }
    }
    return DbType.UNKNOWN;
  }
  private static DbType getDbTypeByDriverName(String dn) throws SQLException {
    val driverName = dn.toUpperCase();
    if (driverName.contains("MYSQL") || driverName.contains("MARIADB")) {
      return DbType.MYSQL;
    } else if (driverName.contains("ORACLE")) {
      return DbType.ORACLE;
    } else if (driverName.contains("KINGBASE")) {
      return DbType.KINGBASE;
    } else if (driverName.contains("DMDRIVER")) {
      return DbType.DM;
    } else if (driverName.contains("OSCAR")) {
      return DbType.SHENTONG;
    } else if (driverName.contains("POSTGRESQL")) {
      return DbType.POSTGRESQL;
    }

    return DbType.UNKNOWN;
  }
}
