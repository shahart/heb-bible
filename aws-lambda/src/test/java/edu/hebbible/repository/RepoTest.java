package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RepoTest {

    Repo repo = new Repo();

    @Test
    public void init() {
        Collection<Pasuk> store = repo.getStore();
        assertEquals(23_204, store.size());

        assertTrue(store.iterator().next().text().contains("בראשית ברא"));
        
        Pasuk last = repo.getStore().get(23_204 - 1);
        assertTrue(last.text().endsWith("ויעל"));
        assertEquals(38, last.book());
        assertEquals(36, last.perek());
        assertEquals(23, last.pasuk());
    }

    @Test
    public void torahAllCompare() {
        Pasuk prevPasuk = null;
        for (Pasuk pasuk: repo.getStore()) {
            if (pasuk.book() == 5) { // end of Torah, 5 chumashim
                assertEquals(pasuk.perek(), 1);
                assertEquals(pasuk.pasuk(), 1);
                break;
            }
            prevPasuk = pasuk;
        }
        assertEquals(prevPasuk.perek(), 34);
        assertEquals(prevPasuk.pasuk(), 12);
    }
}
