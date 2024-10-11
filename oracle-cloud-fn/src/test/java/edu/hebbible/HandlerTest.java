package edu.hebbible;

import com.fnproject.fn.testing.*;
import org.junit.*;

import static org.junit.Assert.*;

public class HandlerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnPsukim() {
        testing.givenEvent().withBody("{\"name\":\"ajr=\"}").enqueue();
        testing.thenRun(Handler.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("Total Psukim: 25", result.getBodyAsString());
    }
}
