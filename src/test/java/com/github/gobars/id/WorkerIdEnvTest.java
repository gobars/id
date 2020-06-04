package com.github.gobars.id;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.junit.Assert.assertEquals;

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
