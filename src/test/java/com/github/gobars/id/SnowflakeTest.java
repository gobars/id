package com.github.gobars.id;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    val conf = new Snowflake.Conf(Snowflake.fromSpec(Id.SPEC));
    val sf = new TimebackSnowflake(conf, 0);

    sf.currentMillis = System.currentTimeMillis();
    long id1 = sf.next();

    long backwardId = Util.readBackwardId(0);
    sf.currentMillis -= 10;
    long id2 = sf.next();

    assertTrue(id2 != id1);
    assertTrue(backwardId != Util.readBackwardId(0));
  }

  @Test
  public void conf() {
    val conf = new Snowflake.Conf(Snowflake.fromSpec(Id.SPEC));

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
}
