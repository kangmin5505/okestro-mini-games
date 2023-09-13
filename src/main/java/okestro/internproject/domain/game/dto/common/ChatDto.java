package okestro.internproject.domain.game.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String type;
    private String from;
    private String content;
}
