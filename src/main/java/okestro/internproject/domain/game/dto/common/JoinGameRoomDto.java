package okestro.internproject.domain.game.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinGameRoomDto {
    private UUID userId;
    private String nickname;
    private String email;
}
