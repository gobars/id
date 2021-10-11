package com.github.gobars.id.db;

import com.github.gobars.id.IdNext;
import com.github.gobars.id.conf.ConnGetter;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 从数据库中直接获得Seq.
 *
 * @author bingoobjca
 */
@Data
public class Seq implements IdNext {
  private final Object LOCK = new Object();

  @Accessors(fluent = true)
  private ConnGetter connGetter;

  @Accessors(fluent = true)
  private String name = "default";

  @Accessors(fluent = true)
  private String table = "t_seq";

  private int waterLevel;
  private int step;

  // 最大序列值，从表中取得，0时不设置
  private long maxSeq;
  // 是否循环，结合 maxSeq 使用，从表中取得
  private boolean cycle;

  // 序列1
  private long seq1;
  // 序列2
  private long seq2;
  // 当前序列，初始值0表示未设置，1时当前使用seq1, 2时当前使用seq2
  private int curr;
  // 序列1剩余
  private int avail1;
  // 序列2剩余
  private int avail2;

  // 正在更新的线程
  private Thread thread;

  public long next() {
    synchronized (LOCK) {
      return nextInternal();
    }
  }

  public long nextInternal() {
    if (maxSeq > 0 && (seq1 > maxSeq || seq2 > maxSeq)) {
      throw new OverMaxSeqException();
    }

    for (int retries = 0; retries < 3; retries++) {
      Long v = getNext();
      if (v != null) {
        return v;
      }
    }

    throw new OverTriesException();
  }

  private Long getNext() {
    if (avail1 <= 0 && avail2 <= 0) {
      // 两个序列都没有值，触发同步更新
      updateSync();
    }

    if (curr == 0) {
      curr = avail1 > 0 ? 1 : 2;
    } else if (curr == 1 && avail1 <= 0) {
      curr = 2;
    } else if (curr == 2 && avail2 <= 0) {
      curr = 1;
    }

    long v;
    long avail;
    if (curr == 1) {
      v = seq1;
      seq1++;
      avail = --avail1;
    } else {
      v = seq2;
      seq2++;
      avail = --avail2;
    }

    if (maxSeq > 0 && v > maxSeq) {
      // 达到最大序列
      if (cycle) {
        // 循环从头开始
        resetState();
        resetDb();
        // 返回 null, 重试
        return null;
      }

      throw new OverMaxSeqException();
    }

    if (avail == waterLevel) {
      // 达到水位线，触发异步更新
      updateAsync();
    }

    return v;
  }

  private void resetState() {
    curr = 1;
    seq1 = 0;
    avail1 = 0;
    seq2 = 0;
    avail2 = 0;
  }

  private void updateAsync() {
    Runnable r =
        new Runnable() {
          @Override
          public void run() {
            synchronized (LOCK) {
              updateSync();
            }
          }
        };
    new Thread(r).start();
  }

  private void updateSync() {
    if (avail1 > 0 && avail2 > 0) {
      // 都有值时，不触发更新
      return;
    }

    DbSeq ds = updateDb();
    waterLevel = ds.waterLevel;
    maxSeq = ds.maxSeq;
    cycle = ds.cycle;
    if (avail1 <= 0) {
      seq1 = ds.seq - ds.step;
      avail1 = ds.step;
    } else if (avail2 <= 0) {
      seq2 = ds.seq - ds.step;
      avail2 = ds.step;
    }
  }

  private static class DbSeq implements SqlRunner.RowScanner {
    long seq;
    int step;
    int waterLevel;
    long maxSeq;
    boolean cycle;

    @Override
    public boolean scanRow(int rowIndex, ResultSet rs) throws SQLException {
      seq = rs.getLong(1);
      step = rs.getInt(2);
      waterLevel = rs.getInt(3);
      maxSeq = rs.getLong(4);
      cycle = rs.getBoolean(5);
      return false;
    }
  }

  @SneakyThrows
  public void resetDb() {
    @Cleanup val c = connGetter.getConn();
    c.setAutoCommit(true);
    val r = new SqlRunner(c);
    r.update("update " + table + " set seq = start where name = ?", name);
  }

  @SneakyThrows
  public DbSeq updateDb() {
    @Cleanup val c = connGetter.getConn();
    c.setAutoCommit(false);

    val r = new SqlRunner(c);
    val u = "update " + table + " set seq = seq + step where name = ?";
    val s = "select seq, step, water_level, max_seq, cycle from " + table + " where name = ?";

    try {
      int row = r.update(u, name);
      DbSeq dbSeq = new DbSeq();
      if (row > 0) {
        r.select(1, dbSeq, s, name);
        return dbSeq;
      }

      try {
        val i = "insert " + table + "(name) values(?)";
        r.insert(i, name);
      } catch (SQLException e) {
        // ignore
      }

      r.update(u, name);
      r.select(1, dbSeq, s, name);
      return dbSeq;
    } finally {
      c.commit();
    }
  }

  public static class OverMaxSeqException extends RuntimeException {}

  public static class OverTriesException extends RuntimeException {}
}
