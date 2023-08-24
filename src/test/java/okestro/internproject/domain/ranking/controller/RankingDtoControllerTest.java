package okestro.internproject.domain.ranking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okestro.internproject.domain.auth.oauth2.enums.OAuth2Provider;
import okestro.internproject.domain.auth.oauth2.enums.Role;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.service.GameService;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class RankingDtoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    GameService gameService;

    @Autowired
    UserService userService;

    @Autowired
    UserGameStatService userGameRecordService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getRanking() throws Exception {
        User test1 = createUser("test1");
        User test2 = createUser("test2");
        User test3 = createUser("test3");
        User test4 = createUser("test4");

        for (int i = 0; i < 23; ++i) {
            userService.save(createUser("test" + (i + 5)));
        }


        userGameRecordService.update(test1.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test1.getId(), GameTitle.CARD_MATCHING, false);

        userGameRecordService.update(test2.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test2.getId(), GameTitle.CARD_MATCHING, false);
        userGameRecordService.update(test2.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test2.getId(), GameTitle.CARD_MATCHING, false);

        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, false);
        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, true);
        userGameRecordService.update(test3.getId(), GameTitle.CARD_MATCHING, false);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/rankings?page=2"))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
    }

    private User createUser(String nickname) {
        User user = User.builder()
                .nickname(nickname)
                .email(nickname + "@google.com")
                .role(Role.USER)
                .oAuth2Provider(OAuth2Provider.GOOGLE)
                .avatarURL("")
                .build();
        User savedUser = userService.save(user);

        userGameRecordService.create(savedUser);
        return user;
    }
}