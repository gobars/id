package com.github.gobars.id;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.conf.ConnGetter;
import com.github.gobars.id.db.SnowflakeDb;
import com.github.gobars.id.db.WorkerIdDb;
import com.github.gobars.id.util.Util;
import java.io.File;
import java.security.SecureRandom;
import lombok.val;
import org.junit.Test;

public class SnowflakeTest {
  @Test
  public void saveCurrentMillis() {
    long randWorkerId = new SecureRandom().nextLong();
    assertEquals(0, Util.readBackwardId(randWorkerId));

    Util.saveBackwardId(randWorkerId, 1);
    assertEquals(1, Util.readBackwardId(randWorkerId));

    new File(Util.backwardIdFile(randWorkerId)).delete();
  }

  @Test
  public void backward() {
    val conf = Conf.fromSpec(Id.SPEC);
    val sf = new TimebackSnowflake(conf, 0);

    sf.currentMillis = System.currentTimeMillis();
    long id1 = sf.next();

    long backwardId = Util.readBackwardId(0);
    sf.currentMillis -= 10;
    long id2 = sf.next();

    assertNotEquals(id1, id2);
    assertNotEquals(backwardId, Util.readBackwardId(0));
  }

  @Test
  public void backwardDb() {
    val conf = Conf.fromSpec(Id.SPEC);

    val url = "jdbc:mysql://localhost:3306/id?useSSL=false";
    val ds = new ConnGetter.JdbcConnGetter(url, "root", "root");
    val workIdDb = new WorkerIdDb().connGetter(ds).biz("default");

    val sf = new TimebackSnowflakeDb(conf, workIdDb);

    sf.currentMillis = System.currentTimeMillis();
    long id1 = sf.next();

    sf.currentMillis -= 10;
    long id2 = sf.next();

    assertNotEquals(id1, id2);
  }

  @Test
  public void conf() {
    val conf = Conf.fromSpec(Id.SPEC);

    assertEquals(8, conf.getWorkerBits());
    assertEquals(255, conf.getMaxWorkerId());
    assertEquals(4095, conf.getMaxSequence());
  }

  static class TimebackSnowflake extends Snowflake {
    long currentMillis;

    public TimebackSnowflake(Conf conf, long workerId) {
      super(conf, workerId);
    }

    @Override
    protected long currentTimeMillis() {
      return currentMillis;
    }
  }

  static class TimebackSnowflakeDb extends SnowflakeDb {
    long currentMillis;

    public TimebackSnowflakeDb(Conf conf, WorkerIdDb workerIdDb) {
      super(conf, workerIdDb);
    }

    @Override
    protected long currentTimeMillis() {
      return currentMillis;
    }
  }
}
