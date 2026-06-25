package edu.hebbible.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SvcTest {

    @Autowired
    private lateinit var svc: Svc

    @Test
    fun psukim() {
        val actual = svc.psukim("שחר", false, false)
        assertEquals(25, actual.size)
    }
}
