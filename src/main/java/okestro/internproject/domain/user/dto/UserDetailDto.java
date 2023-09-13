package okestro.internproject.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.user.entity.db.User;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDetailDto {
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static UserDetailDto createUserDetailDto(User user) {
        return UserDetailDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
