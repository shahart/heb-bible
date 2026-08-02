package edu.hebbible;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hebbible.auth.AuthenticatedUser;
import edu.hebbible.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.jmx.enabled=true"
)
class JolokiaIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Test
    void readsPsukimCountOverHttp() throws Exception {
        String token = jwtService.createToken(
                new AuthenticatedUser("local:test", "Test User", "test@example.com"));
        URI uri = URI.create("http://127.0.0.1:" + port
                + "/actuator/jolokia/read/edu.hebbible:type=Psukim/PsukimCount");

        HttpResponse<Void> anonymousResponse = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(uri).GET().build(),
                        HttpResponse.BodyHandlers.discarding());
        assertEquals(302, anonymousResponse.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = new ObjectMapper().readTree(response.body());

        assertEquals(200, response.statusCode(), response.body());
        assertEquals(200, body.path("status").asInt());
        assertEquals(23_204, body.path("value").asInt());
    }
}
