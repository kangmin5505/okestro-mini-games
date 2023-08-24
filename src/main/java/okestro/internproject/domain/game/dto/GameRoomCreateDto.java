package okestro.internproject.domain.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class GameRoomCreateDto {

    @NotNull(message = "게임방 제목을 입력해주세요.")
    @Length(min = 1, max = 10, message = "게임방 제목은 1 ~ 10자 이내로 입력해주세요.")
    private String title;
}
