package okestro.internproject.domain.game.repository.stompRepository;

import okestro.internproject.domain.game.entity.memory.GameRoom;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CardMatchingRepository implements StompRepository {
    private final Map<UUID, GameRoom> cardMatchingRooms = new ConcurrentHashMap<>();

    public void save(GameRoom gameRoom) {
        cardMatchingRooms.put(gameRoom.getGameRoomInfo().getId(), gameRoom);
    }

    public List<GameRoom> findAll() {
        return new ArrayList<>(cardMatchingRooms.values());
    }

    public void deleteById(UUID gameRoomId) {
        cardMatchingRooms.remove(gameRoomId);
    }

    public Optional<GameRoom> findById(UUID gameRoomId) {
        return Optional.ofNullable(cardMatchingRooms.get(gameRoomId));
    }
}
