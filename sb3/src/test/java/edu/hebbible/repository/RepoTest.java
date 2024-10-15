package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.impl.ServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void engTx() {
        assertEquals("שחר", ServiceImpl.engTx("שחר"));
        assertEquals("שחר", ServiceImpl.engTx("%D7%A9%D7%97%D7%A8="));
    }
}
