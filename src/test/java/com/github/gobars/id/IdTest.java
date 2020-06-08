package com.github.gobars.id;

import static org.junit.Assert.assertTrue;

import com.github.gobars.id.db.DbId;
import org.junit.Test;

public class IdTest {
  @Test
  public void next() {
    assertTrue(Id.next() > 0);
  }

  @Test
  public void dbid() {
    assertTrue(DbId.next() > 0);
  }
}
