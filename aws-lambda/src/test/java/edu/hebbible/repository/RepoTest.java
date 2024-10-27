package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RepoTest {

    @Test
    public void init() {
        Repo repo = new Repo();
        Collection<Pasuk> store = repo.getStore();
        assertEquals(23_204, store.size());

        assertTrue(store.iterator().next().text().contains("בראשית ברא"));
        
        Pasuk last = repo.getStore().get(23_204 - 1);
        assertEquals(36, last.perek());
        assertEquals(23, last.pasuk());
        assertEquals(38, last.book());
    }
}
