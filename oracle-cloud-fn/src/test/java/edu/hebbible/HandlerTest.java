package edu.hebbible;

import com.fnproject.fn.testing.*;
import org.junit.*;

import static org.junit.Assert.*;

public class HandlerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Ignore
    // @Test
    public void shouldReturnPsukim() {
        testing.givenEvent().withBody("שחר").enqueue(); // "{\"name\":\"arj\"}").enqueue();
        testing.thenRun(Handler.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("Total Psukim: שחר25", result.getBodyAsString());
    }
}
