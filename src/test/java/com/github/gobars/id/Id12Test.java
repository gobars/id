package com.github.gobars.id;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Id12Test {
  @Test
  public void next() {
    long next = Id12.next();
    System.out.println("Id12: " + next);
    assertTrue(String.valueOf(next).length() <= 12);
  }
}
