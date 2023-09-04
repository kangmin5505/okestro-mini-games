package okestro.internproject.domain.auth.jwt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Data
@NoArgsConstructor
public class JwtUserDto implements SimpleUser {
    private UUID id;
    private String email;
    private String nickname;

    @Builder
    public JwtUserDto(UUID id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
