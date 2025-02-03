package edu.hebbible;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class HandlerTest {

    private Handler handler;

    @Mock
    Context context;

    @Mock
    LambdaLogger loggerMock;

    @Before
    public void setUp() throws Exception {
        when(context.getLogger()).thenReturn(loggerMock);
        doAnswer(call -> {
            System.out.println((String)call.getArgument(0));
            return null;
        }).when(loggerMock).log(anyString());
        handler = new Handler();
    }

    @Test
    public void testHandleRequestFromPostman() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "שחר");
        payload.put("type", "Pasuk");
        payload.put("extra", "containsName-" + false);
        String response = handler.handleRequest(payload, context);
        assertTrue("response: " + response, response.contains(": 25"));
        payload.put("extra", "containsName-" + true);
        response = handler.handleRequest(payload, context);
        assertTrue("response: " + response, response.contains(": 75"));
    }

    @Ignore
    public void testHandleRequestFromBrowser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "שחר");
        Map<String, Object> payload = new HashMap<>();
        payload.put("body", body);
        String response = handler.handleRequest(payload, context);
        assertTrue(response.contains(": 25"));
        body.put("containsName", true);
        response = handler.handleRequest(payload, context);
        assertTrue(response.contains(": 75"));
    }
}
