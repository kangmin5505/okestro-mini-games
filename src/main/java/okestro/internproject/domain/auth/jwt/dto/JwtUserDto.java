package okestro.internproject.domain.auth.jwt.dto;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Data
@Builder
public class JwtUserDto implements SimpleUser {
    private final UUID id;
    private final String email;
    private final String nickname;
}
