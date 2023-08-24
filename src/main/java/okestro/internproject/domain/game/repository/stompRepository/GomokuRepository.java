package okestro.internproject.domain.game.repository.stompRepository;

import okestro.internproject.domain.game.entity.memory.GameRoom;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GomokuRepository implements StompRepository {
    private final Map<UUID, GameRoom> gomokuRooms = new ConcurrentHashMap<>();

    public void save(GameRoom gameRoom) {
        gomokuRooms.put(gameRoom.getGameRoomInfo().getId(), gameRoom);
    }

    public List<GameRoom> findAll() {
        return new ArrayList<>(gomokuRooms.values());
    }

    public void deleteById(UUID gameRoomId) {
        gomokuRooms.remove(gameRoomId);
    }

    public Optional<GameRoom> findById(UUID gameRoomId) {
        return Optional.ofNullable(gomokuRooms.get(gameRoomId));
    }
}
