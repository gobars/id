package com.github.gobars.id.util;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FilesTest {
    @Test
    public void findAvailableWorkerID() {
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(1);
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(101);
        assertThat(Files.findAvailableWorkerID(1)).isEqualTo(102);
    }
}
