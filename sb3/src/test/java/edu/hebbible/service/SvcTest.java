package edu.hebbible.service;

import edu.hebbible.model.Pasuk;
import edu.hebbible.repository.Repo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SvcTest {

    @Autowired
    private Svc svc;

    @Test
    void psukim() {
        List<Pasuk> actual = svc.psukim("שחר", false, false);
        assertEquals(25, actual.size());
    }

    @Disabled
    void dilugim() {
        int actual = svc.dilugim("באי", 1, 50);
        // assertEquals(30, actual);
    }

    /** for JavaScript Impl': <code>let dictionary = new Map();</code>*/
    // @Test
    @Disabled
    public void lookUpTable() {
        Map<String, Integer> res = new TreeMap<>();
        StringBuilder sbI = new StringBuilder();
        StringBuilder sbJ = new StringBuilder();
        int sum = 0;
        for (int i = 0; i < 27; i++) {
            sbI.setLength(0);
            Repo.getHebChar(sbI, i);
            for (int j = 0; j < 27; j++) {
                sbJ.setLength(0);
                Repo.getHebChar(sbJ, j);
                String name = sbI.toString() + sbJ;
                int psukim = svc.psukim(name, false, true).size();
                sum += psukim;
                if (psukim > 0) {
                    System.out.println("dictionary.set(\"" + name +"\", \"" + psukim + "\");");
                    res.put(name, psukim);
                }
            }
        }
        System.out.println("dictionarySize: " + res.size());
        // there are 62 dups
        assertEquals(sum, svc.repoSize());
    }
}
