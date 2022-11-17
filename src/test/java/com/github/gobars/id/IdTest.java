package com.github.gobars.id;

import com.github.gobars.id.conf.Conf;
import com.github.gobars.id.db.DbId;
import org.junit.Test;

import static com.github.gobars.id.Id.SPEC;
import static org.junit.Assert.assertTrue;

public class IdTest {
    @Test
    public void repeated() {
        for (int i = 0; i < 10; i++) {
            Id.next();
        }
    }

    @Test
    public void next() {
        long[] ids =
                new long[]{
                        Id.next(),
                        19789232541306880L,
                        19786145047138304L,
                        19785822010101760L,
                        19784150811590656L,
                        19783774090842112L,
                        19783674007306240L,
                        19783452816490496L,
                        19783351293362176L,
                        19782881404538880L,
                        19781203922014208L,
                        19780868970037248L,
                        19780425225256960L
                };

        for (long id : ids) {
            System.out.println(id);

            Conf conf = Conf.fromSpec(SPEC);
            //    long a = 346944917504L;
            //    long a = 1782105694208L;
            //    long a = 18825076141383680L;
            long a = id;

            System.out.println("conf:" + conf);
            System.out.println(conf.parseID(a));
        }
        assertTrue(Id.next() > 0);
    }

    @Test
    public void dbid() {
        assertTrue(DbId.next() > 0);
    }
}
