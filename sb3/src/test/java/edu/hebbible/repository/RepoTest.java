package edu.hebbible.repository;

import edu.hebbible.model.Pasuk;
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
        assertEquals(39, last.bookNo());

        assertEquals(1196930, repo.getTorTxt().length());
        assertEquals(repo.getTorTxt().length() + 1, repo.getTotalLetters());
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
    // @Test
    void printAll() {
        repo.init();
        // tora - pasuk.bookNo() in 1..5
        // nevi'im - 6..26
        // ktuvim - 27..39
        int half_1_words = 0;
        int half_2_words = 0;
        int which_half_words = 1;

        int half_1_letters = 0;
        int half_2_letters = 0;
        int which_half_letters = 1;

        int half_1_psks = 0;
        int half_2_psks = 0;
        int which_half_psks = 1;

        for (int i = 0; i < repo.getTotalVerses(); ++i) {
            Pasuk pasuk = repo.getStore().get(i);
            if (pasuk.bookNo() == 6) {
                break;
            }
            // words
            if (pasuk.text().contains("דרש דרש")) {
                which_half_words = 2;
                System.out.println(pasuk.bookNo() + ":" + pasuk.perek() + ":" + pasuk.pasuk() + "," + pasuk.text());
            }
            else {
                if (which_half_words == 1) {
                    half_1_words += pasuk.text().split(" ").length;
                }
                else {
                    half_2_words += pasuk.text().split(" ").length;
                }
            }
            // letters
            if (pasuk.text().contains("הולך על גחון")) {
                which_half_letters = 2;
                System.out.println(pasuk.bookNo() + ":" + pasuk.perek() + ":" + pasuk.pasuk() + "," + pasuk.text());
            }
            else {
                if (which_half_letters == 1) {
                    half_1_letters += pasuk.text().replace(" ", "").length();
                }
                else {
                    half_2_letters += pasuk.text().replace(" ", "").length();
                }
            }
            // psks
            if (pasuk.text().contains("וישמע משה וייטב בעיניו")) {
                which_half_psks = 2;
                System.out.println(pasuk.bookNo() + ":" + pasuk.perek() + ":" + pasuk.pasuk() + "," + pasuk.text());
            }
            else {
                if (which_half_psks == 1) {
                    half_1_psks += 1;
                }
                else {
                    half_2_psks += 1;
                }
            }
            // System.out.println(pasuk.bookNo() + ":" + pasuk.perek() + ":" + pasuk.pasuk() + "," + pasuk.text());
        }
        System.out.println("words: " + half_1_words + ":" + half_2_words);
        System.out.println("letters: " + half_1_letters + ":" + half_2_letters);
        System.out.println("psks: " + half_1_psks + ":" + half_2_psks);
    }
}
