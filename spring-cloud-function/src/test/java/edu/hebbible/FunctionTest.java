package edu.hebbible;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hebbible.service.impl.ServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FunctionTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void engTx() throws Exception {
        String arg = "{\"name\":\"שחר\"}";
        assertThat(ServiceImpl.engTx(((Map<String, String>) (new ObjectMapper().readValue(arg, Map.class))).getOrDefault("name", ""))).
                isEqualTo("שחר");
    }

    @Disabled
    public void psukim() throws Exception {
        String arg = "שחר";
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/psukim/" + arg, String.class)).
                isEqualTo("Total psukim: 25");
    }
}
