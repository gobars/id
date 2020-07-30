package com.github.gobars.id.util;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FilesTest {
  @Test
  public void findAvailableWorkerID() {
    assertThat(Files.findAvailableWorkerID(1, true)).isEqualTo(1);
    assertThat(Files.findAvailableWorkerID(1, false)).isEqualTo(2);
    assertThat(Files.findAvailableWorkerID(1, false)).isEqualTo(3);

    assertThat(Files.findAvailableWorkerID(10, true)).isEqualTo(10);
    assertThat(Files.findAvailableWorkerID(10, false)).isEqualTo(20);
    assertThat(Files.findAvailableWorkerID(10, false)).isEqualTo(30);

    assertThat(Files.findAvailableWorkerID(115, true)).isEqualTo(115);
    assertThat(Files.findAvailableWorkerID(115, false)).isEqualTo(130);
    assertThat(Files.findAvailableWorkerID(115, false)).isEqualTo(145);

    assertThat(Files.findAvailableWorkerID(117, true)).isEqualTo(117);
    assertThat(Files.findAvailableWorkerID(117, false)).isEqualTo(134);
    assertThat(Files.findAvailableWorkerID(117, false)).isEqualTo(151);

    assertThat(Files.findAvailableWorkerID(8, true)).isEqualTo(8);
    assertThat(Files.findAvailableWorkerID(8, false)).isEqualTo(16);
    assertThat(Files.findAvailableWorkerID(8, false)).isEqualTo(24);

    assertThat(Files.tryAvailableLocalWorker(true)).isGreaterThan(0);
  }
}
