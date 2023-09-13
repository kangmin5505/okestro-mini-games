package okestro.internproject.domain.game.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    @PostConstruct
    public void init() {
        gameRepository.save(new Game(GameTitle.CARD_MATCHING));
        gameRepository.save(new Game(GameTitle.GOMOKU));
    }

    public List<Game> findAllGame() {
        return gameRepository.findAll();
    }

    public Optional<Game> findByGameTitle(GameTitle gameTitle) {
        return gameRepository.findById(gameTitle);
    }

    public Optional<Game> findGame(GameTitle gameTitle) {
        return gameRepository.findById(gameTitle);
    }
}
