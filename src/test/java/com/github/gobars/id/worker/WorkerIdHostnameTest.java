package com.github.gobars.id.worker;

import com.google.common.truth.Truth;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class WorkerIdHostnameTest {
  @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Test
  public void hostname() {

    Truth.assertThat(WorkerIdHostname.HOSTNAME).isNotEmpty();
    Truth.assertThat(WorkerIdHostname.parseWorkerId("ab-01")).isEqualTo(1);

    environmentVariables.set("HOSTNAME", "BINGOOHOST1");
    Truth.assertThat(WorkerIdHostname.getHostname()).isEqualTo("BINGOOHOST1");

    environmentVariables.set("COMPUTERNAME", "BINGOOHOST2");
    Truth.assertThat(WorkerIdHostname.getHostname()).isEqualTo("BINGOOHOST2");

    Truth.assertThat(WorkerIdHostname.parseWorkerId("19216856102")).isEqualTo(216856102);
  }
}
