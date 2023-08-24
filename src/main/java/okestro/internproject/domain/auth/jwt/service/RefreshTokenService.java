package okestro.internproject.domain.auth.jwt.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.entity.RefreshToken;
import okestro.internproject.domain.auth.jwt.repository.RefreshTokenRepository;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public Optional<RefreshToken> findByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void storeRefreshToken(String refreshToken, UUID userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));

        Optional<RefreshToken> refreshTokenOptional = findByUserId(userId);
        if (refreshTokenOptional.isPresent()) {
            refreshTokenOptional.get().update(refreshToken);
        } else {
            save(RefreshToken.builder()
                    .token(refreshToken)
                    .user(user)
                    .build()
            );
        }
    }
}
