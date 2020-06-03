package cn.bjca.id;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WorkerIdRandomTest {

  @Test
  public void workerId() {
    assertTrue(new WorkerIdRandom().workerId() > 0);
  }
}
