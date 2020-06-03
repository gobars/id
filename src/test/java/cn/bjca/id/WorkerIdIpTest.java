package cn.bjca.id;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WorkerIdIpTest {

  @Test
  public void getWorkerId() {
    assertTrue(new WorkerIdIp().workerId() > 0);
  }
}
