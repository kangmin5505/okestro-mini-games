package okestro.internproject.domain.game.service;

import okestro.internproject.domain.auth.oauth2.enums.OAuth2Provider;
import okestro.internproject.domain.auth.oauth2.enums.Role;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@Transactional
class UserGameRecordDtoGameStatServiceTest {

    @Autowired
    UserGameRecordService userGameStatService;

    @Autowired
    GameService gameService;

    @Autowired
    UserService userService;


    @Test
    void findAllByUser() {
        Optional<Game> gameOptional = gameService.findByGameTitle(GameTitle.CARD_MATCHING);


        User user1 = createUser("user1");
        User user2 = createUser("user2");
        User user3 = createUser("user3");

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        UserGameRecord userGameStat = UserGameRecord.builder()
                .game(gameOptional.get())
                .winUser(user1)
                .winUserScore(10)
                .loseUser(user2)
                .loseUserScore(5)
                .finishTime(LocalDateTime.now().plusMinutes(5L))
                .startTime(LocalDateTime.now())
                .build();
        userGameStatService.save(userGameStat);

        UserGameRecord userGameRecord = UserGameRecord.builder()
                .game(gameOptional.get())
                .winUser(user1)
                .winUserScore(10)
                .loseUser(user3)
                .loseUserScore(5)
                .finishTime(LocalDateTime.now().plusMinutes(5L))
                .startTime(LocalDateTime.now())
                .build();
        userGameStatService.save(userGameRecord);

        UserGameRecord userGameStat23 = UserGameRecord.builder()
                .game(gameOptional.get())
                .winUser(user2)
                .winUserScore(10)
                .loseUser(user1)
                .loseUserScore(5)
                .finishTime(LocalDateTime.now().plusMinutes(5L))
                .startTime(LocalDateTime.now())
                .build();
        userGameStatService.save(userGameStat23);
    }

    @Test
    void save() {
        Optional<Game> gameOptional = gameService.findByGameTitle(GameTitle.CARD_MATCHING);


        User user1 = createUser("user1");
        User user2 = createUser("user2");

        userService.save(user1);
        userService.save(user2);

        UserGameRecord userGameRecord = UserGameRecord.builder()
                .game(gameOptional.get())
                .winUser(user1)
                .winUserScore(10)
                .loseUser(user2)
                .loseUserScore(5)
                .finishTime(LocalDateTime.now().plusMinutes(5L))
                .startTime(LocalDateTime.now())
                .build();
        UserGameRecord result = userGameStatService.save(userGameRecord);

        Assertions.assertThat(result).isEqualTo(userGameRecord);
    }

    private User createUser(String username) {
        return User.builder()
                .avatarURL("")
                .email(String.format("%s@google.com", username))
                .nickname(username)
                .role(Role.USER)
                .oAuth2Provider(OAuth2Provider.GOOGLE)
                .build();
    }
}