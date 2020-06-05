package com.github.gobars.id;

import java.sql.*;
import java.util.*;
import lombok.Cleanup;
import lombok.Value;
import lombok.val;

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

    List<String> columns = new ArrayList<String>();
    val rsmd = rs.getMetaData();
    for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
      columns.add(rsmd.getColumnLabel(i + 1));
    }

    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    while (rs.next()) {
      Map<String, String> row = new HashMap<String, String>();
      for (int i = 0, n = columns.size(); i < n; i++) {
        row.put(columns.get(i).toLowerCase(Locale.ENGLISH), rs.getString(i + 1));
      }
      list.add(row);
    }

    return list;
  }
}
