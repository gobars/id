package cn.bjca.id;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdTest {
  @Test
  public void next() {
    assertEquals(0, Id.next());
  }
}
