package okestro.internproject.domain.user.repository;

import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.user.entity.db.UserGameStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGameStatRepository extends JpaRepository<UserGameStat, Long> {
    Optional<UserGameStat> findByUserIdAndGameTitle(UUID userId, GameTitle gameTitle);

    Optional<List<UserGameStat>> findAllByUserId(UUID userId);

    Page<UserGameStat> findByGameTitle(GameTitle gameTitle, Pageable pageable);
}
