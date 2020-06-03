package cn.bjca.id;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

public class SystemPropertyWorkerIdTest {
  @Rule
  public final ProvideSystemProperty provideSystemProperty =
      new ProvideSystemProperty(WorkerIdEnv.WORKER_ID, "12");

  @Test
  public void test() {
    assertThat(new WorkerIdEnv().workerId()).isEqualTo(12);
  }
}
