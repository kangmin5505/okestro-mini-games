package okestro.internproject.domain.user.repository;

import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGameRecordRepository extends JpaRepository<UserGameRecord, Long> {
    Page<UserGameRecord> findAllByWinUserOrLoseUser(User winUser, User loseUser, Pageable pageable);

    List<UserGameRecord> findAllByWinUserAndLoseUser(User winUser, User loseUser);

}
