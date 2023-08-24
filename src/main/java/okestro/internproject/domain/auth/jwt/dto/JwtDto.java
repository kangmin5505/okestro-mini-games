package okestro.internproject.domain.auth.jwt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtDto {
    private final String accessToken;
    private final String refreshToken;
}
