package okestro.internproject.domain.game.dto.common;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.game.enums.MessageType;

@Data
@Builder
public class MessageDto {
    private MessageType type;
    private Object content;
}