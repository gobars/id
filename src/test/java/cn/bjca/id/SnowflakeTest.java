package cn.bjca.id;

import static cn.bjca.id.Snowflake.workerBackwardIdFile;
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
    assertEquals(0, Snowflake.readBackwardId(randWorkerId));

    Snowflake.saveBackwardId(randWorkerId, 1);
    assertEquals(1, Snowflake.readBackwardId(randWorkerId));

    new File(workerBackwardIdFile(randWorkerId)).delete();
  }

  @Test
  public void backward() {
    val sf = new TimebackSnowflake(new Snowflake.Conf(), 0);

    sf.currentMillis = System.currentTimeMillis();
    long id1 = sf.next();

    long backwardId = Snowflake.readBackwardId(0);
    sf.currentMillis -= 10;
    long id2 = sf.next();

    assertTrue(id2 != id1);
    assertTrue(backwardId != Snowflake.readBackwardId(0));
  }

  @Test
  public void conf() {
    val conf = new Snowflake.Conf();
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
