package edu.hebbible.main.java.edu.hebbible;

import com.google.cloud.functions.invoker.runner.Invoker;
import edu.hebbible.Function;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {

  @Test
  public void test() throws Exception {
    Invoker invoker = new Invoker(8081, "edu.hebbible.Function", "http", Thread.currentThread().getContextClassLoader());
    invoker.startTestServer();

    given()
            .queryParam("name", "שחר").
            // queryParam("containsName", "false").
    when()
            .get("http://localhost:8081").
    then()
            .statusCode(200)
            .body(is("Total Psukim: 25"));

    invoker.stopServer();
  }

  @Disabled
  public void psukim() {
    assertEquals(75, new Function().psukim("שחר", true).size());
  }
}
