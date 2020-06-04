package com.github.gobars.id;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

import static com.google.common.truth.Truth.assertThat;

public class SystemPropertyWorkerIdTest {
  @Rule
  public final ProvideSystemProperty provideSystemProperty =
      new ProvideSystemProperty(WorkerIdEnv.WORKER_ID, "12");

  @Test
  public void test() {
    assertThat(new WorkerIdEnv().workerId()).isEqualTo(12);
  }
}
