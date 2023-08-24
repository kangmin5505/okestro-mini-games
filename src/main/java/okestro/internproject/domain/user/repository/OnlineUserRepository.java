package okestro.internproject.domain.user.repository;

import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OnlineUserRepository {
    private final Map<UUID, OnlineUser> onlineUsers = new ConcurrentHashMap<>();

    // 접속시
    // 기존 유저 연결 끊기
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

    public List<OnlineUser> findAllInLobby() {
        return onlineUsers.values().stream()
                .filter(onlineUser -> onlineUser.getGameTitle() == null
                        && onlineUser.getGameRoomId() == null)
                .collect(Collectors.toList());
    }

    public List<OnlineUser> findAll() {
        return new ArrayList<>(onlineUsers.values());
    }

    public long count() {
        return onlineUsers.size();
    }

    public void deleteById(UUID userId) {
        onlineUsers.remove(userId);
    }

}
