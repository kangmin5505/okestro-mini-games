package okestro.internproject.domain.ranking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.ranking.dto.RankingPageDto;
import okestro.internproject.domain.ranking.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@Slf4j
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public RankingPageDto getRankings(@RequestParam(defaultValue = "card-matching", required = false) GameTitle gameTitle,
                                      @RequestParam(defaultValue = "wins", required = false) String filter,
                                      @RequestParam(defaultValue = "true", required = false) Boolean desc,
                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                      @RequestParam(defaultValue = "10", required = false) Integer limit
    ) {
        return rankingService.findRankingPage(gameTitle, filter, desc, page, limit);
    }

}
