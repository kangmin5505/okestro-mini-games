package okestro.internproject.domain.game.repository;

import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.enums.GameTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, GameTitle> {
}
