package edu.hebbible.controller;

import edu.hebbible.config.SecurityConfig;
import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
@Import(SecurityConfig.class)
public class ControllerTest {

    @MockBean
    Svc service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getPsukim() throws Exception {
        Mockito.when(service.repoSize()).thenReturn(18);

        mvc.perform(get("/psukim").with(oauth2Login())).
                andExpect(status().isOk()).
                andExpect(content().string("18")).
                andDo(print());
    }

    @Test
    void postPsukim() throws Exception {
        List<Pasuk> res = List.of(new Pasuk("משלי", -1, 12, 19, "שפת אמת תכון לעד ועד ארגיעה לשון שקר", 0));
        Mockito.when(service.psukim(anyString(), anyBoolean(), anyBoolean())).thenReturn(res);

        mvc.perform(post("/psukim").
                        with(oauth2Login()).
                        contentType(MediaType.APPLICATION_JSON).
                        content("שחר")).
                andExpect(status().isOk()).
                andExpect(content().string(StringContains.containsString(
                        Integer.toString(res.getFirst().pasuk())))).
                andDo(print());
    }

    @Test
    void anonymousRequestsAreRedirectedToLogin() throws Exception {
        mvc.perform(get("/psukim")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    void anonymousPostRequestsAreRedirectedToLogin() throws Exception {
        mvc.perform(post("/psukim").
                        contentType(MediaType.APPLICATION_JSON).
                        content("שחר")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    void logoutRedirectsToLoggedOutLoginPage() throws Exception {
        mvc.perform(post("/logout").with(oauth2Login())).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/logged-out.html")).
                andExpect(cookie().maxAge("JSESSIONID", 0)).
                andExpect(cookie().maxAge("XSRF-TOKEN", 0));
    }

    @Test
    void loggedOutSessionCannotUsePsukim() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mvc.perform(get("/psukim").
                        session(session).
                        with(oauth2Login())).
                andExpect(status().isOk());

        mvc.perform(post("/logout").session(session)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/logged-out.html"));

        mvc.perform(get("/psukim").session(session)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    void loggedOutPageIsPublic() throws Exception {
        mvc.perform(get("/logged-out.html")).
                andExpect(status().isOk());
    }

}
