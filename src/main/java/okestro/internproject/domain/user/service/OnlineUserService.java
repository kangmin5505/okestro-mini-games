package okestro.internproject.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.repository.OnlineUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineUserService {

    private final OnlineUserRepository onlineUserRepository;

    public boolean isOnlineUser(UUID userId) {
        return onlineUserRepository.findById(userId).isPresent();
    }

    public void addOnlineUser(SimpleUser user) {
        OnlineUser onlineUser = OnlineUser.builder()
                .user(user)
                .gameRoomId(null)
                .gameTitle(null)
                .build();
        onlineUserRepository.save(onlineUser);
    }

    public void joinGameRoom(SimpleUser user, String gameTitle, UUID gameRoomId) {
        onlineUserRepository.updateGameRoomInfo(user.getId(), gameTitle, gameRoomId);
    }

    public void exitGameRoom(UUID userId) {
        onlineUserRepository.updateGameRoomInfo(userId, null, null);
    }

    public boolean isInGameRoom(UUID userId) {
        OnlineUser onlineUser = onlineUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        return !(onlineUser.getGameRoomId() == null && onlineUser.getGameTitle() == null);
    }

    public void removeOnlineUser(SimpleUser user) {
        List<OnlineUser> all = onlineUserRepository.findAll();
        onlineUserRepository.deleteById(user.getId());
    }

    public Optional<OnlineUser> findByUserId(UUID userId) {
        return onlineUserRepository.findById(userId);
    }
}
