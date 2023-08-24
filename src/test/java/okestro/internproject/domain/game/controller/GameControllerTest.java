package okestro.internproject.domain.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.game.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;
    @MockBean
    private JwtProvider jwtProvider;

    private final String secret = "eyJhbGciOiJIUzI1NiJ9.eyJuaWNrbmFtZSI6ImttLmtpbTIiLCJ1c2VySWQiOiI1YmFjNzZhNi1iMjNjLTRmYmQtYjAzYS03OWM2YTkxNTkxZjAiLCJlbWFpbCI6ImttLmtpbTJAb2tlc3Ryby5jb20iLCJpYXQiOjE2OTA4NzQzMTUsImV4cCI6MTY5MDg3NjExNX0.bufc7dUYF6UdHM1er3Ay_YNqmCRFlFkYHQxJXDWh-Ag";

    @Test
    @WithMockUser
    void simpleTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void getGames() throws Exception {
        String gameTitle = "omok";

        // Create the request body data as a Map
        Map<String, String> requestBodyData = new HashMap<>();
        requestBodyData.put("title", "testTitle");

        // Perform the POST request and set the cookie value using the cookie method
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/games/" + gameTitle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBodyData))
                .cookie(new Cookie(JwtProvider.TYPE_ACCESS, secret));

        mockMvc.perform(requestBuilder.with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    void parameterTest() {
    }
}