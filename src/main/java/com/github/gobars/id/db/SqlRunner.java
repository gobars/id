package com.github.gobars.id.db;

import java.sql.*;
import java.util.*;
import lombok.Cleanup;
import lombok.Value;
import lombok.val;

/**
 * SQL执行器.
 *
 * <p>from
 * https://github.com/mybatis/mybatis-3/blob/master/src/main/java/org/apache/ibatis/jdbc/SqlRunner.java
 */
@Value
public class SqlRunner {
  Connection cnn;

  /**
   * Executes an INSERT statement.
   *
   * @param sql The SQL
   * @param args The arguments to be set on the statement.
   * @return The number of rows impacted or BATCHED_RESULTS if the statements are being batched.
   * @throws SQLException If statement preparation or execution fails
   */
  public int insert(String sql, Object... args) throws SQLException {
    @Cleanup val ps = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

    setParameters(ps, args);
    ps.executeUpdate();

    return parseGeneratedKey(ps);
  }

  private int parseGeneratedKey(PreparedStatement ps) throws SQLException {
    val keys = getResults(ps.getGeneratedKeys());
    if (keys.isEmpty()) {
      return -1;
    }

    Iterator<String> i = keys.get(0).values().iterator();
    if (!i.hasNext()) {
      return -1;
    }

    String genkey = i.next();
    if (genkey == null) {
      return -1;
    }

    try {
      return Integer.parseInt(genkey);
    } catch (NumberFormatException e) {
      // ignore, no numeric key support
    }

    return -1;
  }

  private void setParameters(PreparedStatement ps, Object... args) throws SQLException {
    for (int i = 0, n = args.length; i < n; i++) {
      ps.setObject(i + 1, args[i]);
    }
  }

  private List<Map<String, String>> getResults(ResultSet resultSet) throws SQLException {
    @Cleanup val rs = resultSet;

    List<String> cols = new ArrayList<String>();
    val md = rs.getMetaData();
    for (int i = 0, n = md.getColumnCount(); i < n; i++) {
      cols.add(md.getColumnLabel(i + 1));
    }

    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    while (rs.next()) {
      Map<String, String> row = new HashMap<String, String>();
      for (int i = 0, n = cols.size(); i < n; i++) {
        String name = cols.get(i).toLowerCase(Locale.ENGLISH);
        String value = rs.getString(i + 1);
        row.put(name, value);
      }
      list.add(row);
    }

    return list;
  }
}
