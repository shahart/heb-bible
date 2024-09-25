package edu.hebbible.controller;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
public class ControllerTest {

    @MockBean
    Svc service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getPsukim() throws Exception {
        Mockito.when(service.repoSize()).thenReturn(18);

        mvc.perform(get("/psukim")).
                andExpect(status().isOk()).
                andExpect(content().string("18")).
                andDo(print());
    }

    @Test
    void postPsukim() throws Exception {
        List<Pasuk> res = List.of(new Pasuk("משלי", 12, 19, "שפת אמת תכון לעד ועד ארגיעה לשון שקר"));
        Mockito.when(service.psukim(anyString(), anyBoolean())).thenReturn(res);

        mvc.perform(post("/psukim").
                        contentType(MediaType.APPLICATION_JSON).
                        content("שחר")).
                andExpect(status().isOk()).
                andExpect(content().string(StringContains.containsString(
                        Integer.toString(res.getFirst().pasuk())))).
                andDo(print());
    }

}
