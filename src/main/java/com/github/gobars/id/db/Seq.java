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
  private static final int START_SEQ = 10000;

  @Accessors(fluent = true)
  private ConnGetter connGetter;

  @Accessors(fluent = true)
  private String name = "default";

  @Accessors(fluent = true)
  private String table = "t_seq";

  private int waterLevel;
  private int step;

  private long maxSeq;
  private boolean cycle;

  // 当前序列
  private long seq1;
  private long seq2;
  // 当前序列号
  private int curr;
  // 当前序列剩余
  private int avail1;
  private int avail2;

  private boolean updating;

  public long next() {
    synchronized (LOCK) {
      if (maxSeq > 0 && (seq1 > maxSeq || seq2 > maxSeq)) {
        throw new OverMaxSeqException();
      }

      long v = 0;
      long avail = 0;
      for (int retries = 0; retries < 2; retries++) {
        if (avail1 <= 0 && avail2 <= 0) {
          triggerUpdate(false);
        }

        if (curr == 0) {
          curr = avail1 > 0 ? 1 : 2;
        } else if (curr == 1 && avail1 <= 0) {
          curr = 2;
        } else if (curr == 2 && avail2 <= 0) {
          curr = 1;
        }

        if (curr == 1) {
          v = seq1;
          seq1++;
          avail = --avail1;
        } else {
          v = seq2;
          seq2++;
          avail = --avail2;
        }

        if (maxSeq > 0 && v >= maxSeq) {
          if (cycle) {
            resetState();
            resetDb();
            continue;
          }

          throw new OverMaxSeqException();
        }

        break;
      }

      if (avail <= waterLevel) {
        triggerUpdate(true);
      }

      return v;
    }
  }

  private void resetState() {
    curr = 1;
    seq1 = 0;
    avail1 = 0;
    seq2 = 0;
    avail2 = 0;
  }

  private void triggerUpdate(boolean async) {
    if (avail1 > 0 && avail2 > 0) {
      return;
    }

    if (!async) {
      updateSeq();
      return;
    }

    if (updating) {
      updateSeq();
      return;
    }

    new Thread(
            new Runnable() {
              @Override
              public void run() {
                updateSeqLock();
              }
            })
        .start();
  }

  private void updateSeqLock() {
    synchronized (LOCK) {
      updateSeq();
      updating = false;
    }
  }

  private void updateSeq() {
    if (avail1 > 0 && avail2 > 0) {
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
}
