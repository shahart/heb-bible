package edu.hebbible.service.impl;

import edu.hebbible.repository.Repo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

// not SpringBootTest, to save time
public class DilugimTest {

    private ServiceImpl svc = new ServiceImpl();
    Repo repo = new Repo();

    @BeforeEach
    public void before() {
        repo.postConstruct();
        svc.repo = repo;
    }

    @Test
    public void dilugim() {
        // svc.psukim("שחר", false);
        svc.dilugim("באי", 2, 50); // ב ר א ש י ת
        Assertions.assertTrue(svc.dilugim("הץת", 3, 50) >= 1); // והארץהיתה
    }
}
