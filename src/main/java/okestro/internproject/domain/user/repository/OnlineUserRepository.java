package okestro.internproject.domain.user.repository;

import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OnlineUserRepository {
    private final Map<UUID, OnlineUser> onlineUsers = new ConcurrentHashMap<>();

    public void save(OnlineUser onlineUser) {
        onlineUsers.put(onlineUser.getUser().getId(), onlineUser);
    }

    public void updateGameRoomInfo(UUID userId, String gameTitle, UUID gameRoomId) {
        OnlineUser onlineUser = onlineUsers.get(userId);
        if (onlineUser == null) {
            throw new UserException(UserErrorCode.CAN_NOT_FIND_USER);
        }
        onlineUser.updateGameRoom(gameTitle, gameRoomId);
    }

    public Optional<OnlineUser> findById(UUID userId) {
        return Optional.ofNullable(onlineUsers.get(userId));
    }

    public void deleteById(UUID userId) {
        onlineUsers.remove(userId);
    }

    public long count() {
        return onlineUsers.size();
    }

}
