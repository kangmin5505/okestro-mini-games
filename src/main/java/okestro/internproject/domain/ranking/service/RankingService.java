package okestro.internproject.domain.ranking.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.ranking.dto.RankingDto;
import okestro.internproject.domain.ranking.dto.RankingPageDto;
import okestro.internproject.domain.user.entity.db.UserGameStat;
import okestro.internproject.domain.user.service.UserGameStatService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserGameStatService userGameStatService;

    public RankingPageDto findRankingPage(GameTitle gameTitle, String filter, Boolean desc, Integer page, Integer limit) {
        Page<UserGameStat> rankingPage = userGameStatService.findRankingPage(gameTitle, filter, desc, page, limit);
        long totalElements = rankingPage.getTotalElements();

        List<RankingDto> rankings = new ArrayList<>();
        int rank = page * limit + 1;
        for (UserGameStat userGameStat : rankingPage) {
            Integer totalGames = userGameStat.getWins() + userGameStat.getLoses();

            rankings.add(RankingDto.builder()
                    .userId(userGameStat.getUser().getId())
                    .nickname(userGameStat.getUser().getNickname())
                    .rank(rank++)
                    .wins(userGameStat.getWins())
                    .loses(userGameStat.getLoses())
                    .totalGames(totalGames)
                    .winPercentage(getWinPercentage(userGameStat.getWins(), totalGames))
                    .build());
        }
        return RankingPageDto.builder()
                .totalElements(totalElements)
                .rankings(rankings)
                .build();
    }

    private Integer getWinPercentage(Integer wins, Integer totalGames) {
        return totalGames == 0 ? 0 : (int) ((double) wins / totalGames * 100);
    }
}
