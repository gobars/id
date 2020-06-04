package com.github.gobars.id;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IdTest {
  @Test
  public void next() {
    assertTrue(Id.next() > 0);
  }
}
