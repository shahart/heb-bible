package edu.hebbible;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class HebBibleTest {

    @MockBean
    DynamoDbTemplate dynamoDbTemplate;

    @Test
    void contextLoads(ApplicationContext applicationContext) {
        Assertions.assertNotNull(applicationContext);
        Assertions.assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }

}

