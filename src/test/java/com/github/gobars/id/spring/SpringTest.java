package com.github.gobars.id.spring;

import static com.google.common.truth.Truth.assertThat;

import com.github.gobars.id.IdNext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringAppConfig.class)
public class SpringTest {
  @Autowired IdNext idNext;

  @Test
  public void dbid() {
    for (int i = 0; i < 3; i++) {
      assertThat(idNext.next()).isGreaterThan(0);
    }
  }
}
