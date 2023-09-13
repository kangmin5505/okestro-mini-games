package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDtos;
import okestro.internproject.domain.game.service.CardMatchingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CardMatchingStompController {

    private final CardMatchingService cardMatchingService;

    @MessageMapping("/card-matching/{gameRoomId}/check-match")
    public void checkMatch(@DestinationVariable UUID gameRoomId, CardMatchDtos cardMatchDtos) {
        cardMatchingService.checkMatch(gameRoomId, cardMatchDtos);
    }

    @MessageMapping("/card-matching/{gameRoomId}/timeout")
    public void timeout(@DestinationVariable UUID gameRoomId) {
        cardMatchingService.turnTimeout(gameRoomId);
    }

    @MessageMapping("/card-matching/{gameRoomId}")
    public String cardMatching(String payload) {
        return payload;
    }
}
