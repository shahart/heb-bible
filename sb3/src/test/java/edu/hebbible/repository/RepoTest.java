package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.impl.ServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static edu.hebbible.repository.Repo.bookHeb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepoTest {

    Repo repo = new Repo();

    @Test
    public void init() {
        repo.init();
        List<Pasuk> store = repo.getStore();
        assertEquals(23_204, store.size());

        assertTrue(store.getFirst().text().contains("בראשית ברא"));

        Pasuk last = repo.getStore().getLast();
        assertEquals(36, last.perek());
        assertEquals(23, last.pasuk());
        assertEquals("דברי הימים ב", last.book());

        assertEquals(1196930, repo.getTorTxt().length());
        assertEquals(repo.getTorTxt().length() + 1, repo.getTotalLetters());
    }

    @Test
    void engTx() {
        assertEquals("שחר", ServiceImpl.engTx("שחר"));
        assertEquals("שחר", ServiceImpl.engTx("%D7%A9%D7%97%D7%A8="));
    }

    @Test
    void torahAllCompare() {
        repo.init();
        int totVerses = 0;
        Pasuk prevPasuk = null;
        for (Pasuk pasuk: repo.getStore()) {
            ++ totVerses;
            if (pasuk.book().contentEquals(new StringBuilder(bookHeb[5]).reverse())) { // end of Torah, 5 chumashim
                break;
            }
            prevPasuk = pasuk;
        }
        // numbers from dilugim.html - torahAll.html
        assert prevPasuk != null;
        assertEquals(prevPasuk.cntLetter(), 304805+1);
        assertEquals(totVerses, 5844+3); // see http://bible.eliram.net/2016/06/blog-post_50.html
    }

    @Disabled
    void printAll() {
        repo.init();
        // tora - pasuk.bookNo() in 1..5
        // nevi'im - 6..26
        // ktuvim - 27..39
        for (int i = 0; i < repo.getTotalVerses(); ++i) {
            Pasuk pasuk = repo.getStore().get(i);
            System.out.println(pasuk.bookNo() + ":" + pasuk.perek() + ":" + pasuk.pasuk() + "," + pasuk.text());
        }
    }
}
