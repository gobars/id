package cn.bjca.id;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IdTest {
  @Test
  public void next() {
    assertTrue(Id.next() > 0);
  }
}
