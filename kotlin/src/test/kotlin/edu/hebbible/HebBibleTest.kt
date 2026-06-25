package edu.hebbible

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class HebBibleTest {

    @Test
    fun contextLoads(applicationContext: ApplicationContext) {
        Assertions.assertNotNull(applicationContext)
        Assertions.assertTrue(applicationContext.beanDefinitionCount > 0)
    }
}
