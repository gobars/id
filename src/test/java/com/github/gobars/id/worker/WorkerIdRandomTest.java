package com.github.gobars.id.worker;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkerIdRandomTest {

  @Test
  public void workerId() {
    assertTrue(new WorkerIdRandom().workerId() > 0);
  }
}
