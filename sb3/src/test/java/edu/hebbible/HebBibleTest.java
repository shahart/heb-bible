package edu.hebbible;

//import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HebBibleTest {

//    @MockBean
//    DynamoDbTemplate dynamoDbTemplate;

    @Autowired
    private MockMvc mvc;

    @Test
    void contextLoads(ApplicationContext applicationContext) {
        Assertions.assertNotNull(applicationContext);
        Assertions.assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }

    @Test
    void swaggerUiIsPublic() throws Exception {
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

}
