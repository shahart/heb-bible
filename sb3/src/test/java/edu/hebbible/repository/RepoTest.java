package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepoTest {

    @Test
    public void init() {
        Repo repo = new Repo();
        repo.init();
        Collection<Pasuk> store = repo.getStore();
        assertTrue(store.size() > 20_000);
        assertTrue(store.iterator().next().text().contains("בראשית ברא"));
    }
}
