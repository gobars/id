package com.github.gobars.id.worker;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class WorkerIdEnvTest {
  @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Before
  public void before() {
    environmentVariables.set(WorkerIdEnv.WORKER_ID, "13");
  }

  @Test
  public void workerId() {
    assertEquals(13, new WorkerIdEnv().workerId());
  }
}
