package okestro.internproject.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String nickname;
    private String email;

    public static UserDto createUserDto(SimpleUser user) {
        return UserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
