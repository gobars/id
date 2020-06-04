package com.github.gobars.id;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkerIdIpTest {

  @Test
  public void getWorkerId() {
    assertTrue(new WorkerIdIp().workerId() > 0);
  }
}
