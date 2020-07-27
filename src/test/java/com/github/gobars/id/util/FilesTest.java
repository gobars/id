package com.github.gobars.id.util;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FilesTest {
    @Test
    public void findAvailableWorkerID() {
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(1);
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(2);
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(3);

        assertThat(Files.findAvailableWorkerID(10)).isEqualTo(10);
        assertThat(Files.findAvailableWorkerID(10)).isEqualTo(20);
        assertThat(Files.findAvailableWorkerID(10)).isEqualTo(30);

        assertThat(Files.findAvailableWorkerID(115)).isEqualTo(115);
        assertThat(Files.findAvailableWorkerID(115)).isEqualTo(130);
        assertThat(Files.findAvailableWorkerID(115)).isEqualTo(145);

        assertThat(Files.findAvailableWorkerID(117)).isEqualTo(117);
        assertThat(Files.findAvailableWorkerID(117)).isEqualTo(134);
        assertThat(Files.findAvailableWorkerID(117)).isEqualTo(151);

        assertThat(Files.findAvailableWorkerID(8)).isEqualTo(8);
        assertThat(Files.findAvailableWorkerID(8)).isEqualTo(16);
        assertThat(Files.findAvailableWorkerID(8)).isEqualTo(24);
    }
}
