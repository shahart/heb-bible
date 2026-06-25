package edu.hebbible.controller

import edu.hebbible.model.Pasuk
import edu.hebbible.service.Svc
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(Controller::class)
class ControllerTest {

    @MockitoBean
    private lateinit var service: Svc

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun getPsukim() {
        Mockito.`when`(service.repoSize()).thenReturn(18)

        mvc.perform(get("/psukim"))
            .andExpect(status().isOk)
            .andExpect(content().string("18"))
            .andDo(print())
    }

    @Test
    fun postPsukim() {
        val res = listOf(Pasuk("משלי", -1, 12, 19, "שפת אמת תכון לעד ועד ארגיעה לשון שקר", 0))
        Mockito.`when`(service.psukim(anyString(), anyBoolean(), anyBoolean())).thenReturn(res)

        mvc.perform(post("/psukim")
            .contentType(MediaType.APPLICATION_JSON)
            .content("שחר"))
            .andExpect(status().isOk)
            .andExpect(content().string(StringContains.containsString(res.first().pasuk.toString())))
            .andDo(print())
    }
}
