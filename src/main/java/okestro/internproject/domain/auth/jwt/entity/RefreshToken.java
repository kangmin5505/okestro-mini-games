package okestro.internproject.domain.auth.jwt.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter
@ToString
@RedisHash(value = "refreshToken", timeToLive = JwtProvider.REFRESH_TOKEN_EXPIRE_TIME)
public class RefreshToken {

    @Id
    private UUID userId;

    private final String token;

    @Builder
    public RefreshToken(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
