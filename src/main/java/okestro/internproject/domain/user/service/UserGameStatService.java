package okestro.internproject.domain.user.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.service.GameService;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameStat;
import okestro.internproject.domain.user.repository.UserGameStatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGameStatService {

    private final UserGameStatRepository userGameStatRepository;
    private final GameService gameService;


    public Page<UserGameStat> findRankingPage(GameTitle gameTitle, String filter, Boolean desc, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, filter)
                .and(Sort.by(Sort.Direction.ASC, "user.nickname")));

        return userGameStatRepository.findByGameTitle(gameTitle, pageable);
    }

    public Optional<UserGameStat> findByUserIdAndGameTitle(UUID userId, GameTitle gameTitle) {
        return userGameStatRepository.findByUserIdAndGameTitle(userId, gameTitle);
    }

    public Optional<List<UserGameStat>> findAllByUserId(UUID userId) {
        return userGameStatRepository.findAllByUserId(userId);
    }

    public void create(User user) {
        Arrays.stream(GameTitle.values())
                .map(gameService::findByGameTitle)
                .filter(Optional::isPresent)
                .forEach((game) ->
                        userGameStatRepository.save(
                                UserGameStat.builder()
                                        .user(user)
                                        .game(game.get())
                                        .wins(0)
                                        .loses(0)
                                        .totalGames(0)
                                        .winPercentage(0)
                                        .build()
                        )
                );
    }

    public void update(UUID userId, GameTitle gameTitle, boolean isWin) {
        userGameStatRepository.findByUserIdAndGameTitle(userId, gameTitle)
                .ifPresent((gameStat) -> {
                    if (isWin) {
                        gameStat.updateWins();
                    } else {
                        gameStat.updateLoses();
                    }
                });
    }
}
