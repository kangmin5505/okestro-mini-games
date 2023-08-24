package okestro.internproject.domain.game.repository.stompRepository;

import okestro.internproject.domain.game.entity.memory.GameRoom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StompRepository {
    void save(GameRoom gameRoom);
    List<GameRoom> findAll();
    void deleteById(UUID gameRoomId);
    Optional<GameRoom> findById(UUID gameRoomId);
}
