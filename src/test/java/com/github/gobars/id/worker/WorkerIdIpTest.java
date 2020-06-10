package com.github.gobars.id.worker;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkerIdIpTest {

  @Test
  public void getWorkerId() {
    assertTrue(new WorkerIdIp().workerId() > 0);
  }
}
