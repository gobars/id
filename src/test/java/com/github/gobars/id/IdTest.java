package com.github.gobars.id;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.db.DbId;
import org.junit.Test;

import static com.github.gobars.id.Id.SPEC;
import static org.junit.Assert.assertTrue;

public class IdTest {
  @Test
  public void next() {
    long id = Id.next();
    System.out.println(id);

    Conf conf = Conf.fromSpec(SPEC);
    //    long a = 346944917504L;
    //    long a = 1782105694208L;
    //    long a = 18825076141383680L;
    long a = id;

    System.out.println("conf:" + conf);
    System.out.println(conf.parseID(a));
    assertTrue(Id.next() > 0);
  }

  @Test
  public void dbid() {
    assertTrue(DbId.next() > 0);
  }
}
