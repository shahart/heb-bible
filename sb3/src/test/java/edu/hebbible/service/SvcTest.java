package edu.hebbible.service;

import edu.hebbible.model.Pasuk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SvcTest {

    @Autowired
    private Svc svc;

    @Test
    void psukim() {
        List<Pasuk> actual = svc.psukim("שחר", false);
        assertEquals(25, actual.size());
    }

}
