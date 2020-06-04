package com.github.gobars.id;

import com.google.common.truth.Truth;
import org.junit.Test;

public class WorkerIdHostnameTest {

  @Test
  public void hostname() {
    Truth.assertThat(WorkerIdHostname.HOSTNAME).isNotEmpty();
    Truth.assertThat(WorkerIdHostname.parseWorkerId("ab-01")).isEqualTo(1);
  }
}
