package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDto;
import okestro.internproject.domain.game.service.CardMatchingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CardMatchingStompController {

    private final CardMatchingService cardMatchingService;

    @MessageMapping("/card-matching/{gameRoomId}")
    public Map<String, Object> aboutGame(@DestinationVariable UUID gameRoomId, @Payload Map<String, Object> payload) {
        switch (payload.get("type").toString()) {
            case "FLIP_CARD":
                break;
            case "CHECK_MATCH":
                try {
                    cardMatchingService.checkMatch(gameRoomId, convertToCheckCardMatchDtoList(payload.get("content")));
                } catch (ClassCastException e) {
                    log.error("ClassCastException");
                }
                break;
            case "TURN_CHANGE":
                break;
            case "TIMEOUT":
                cardMatchingService.turnTimeout(gameRoomId);
                break;
        }
        return payload;
    }

    private List<CardMatchDto> convertToCheckCardMatchDtoList(Object contentObject) {
        Map<String, Map<String, Object>> content = (Map<String, Map<String, Object>>) contentObject;

        Map<String, Object> card1Data = content.get("card1");
        CardMatchDto card1 = CardMatchDto.builder()
                .position((Integer) card1Data.get("position"))
                .faceValue((String) card1Data.get("faceValue"))
                .build();
        Map<String, Object> card2Data = content.get("card2");
        CardMatchDto card2 = CardMatchDto.builder()
                .position((Integer) card2Data.get("position"))
                .faceValue((String) card2Data.get("faceValue"))
                .build();

        List<CardMatchDto> cardMatchDtoList = new ArrayList<>();
        cardMatchDtoList.add(card1);
        cardMatchDtoList.add(card2);

        return cardMatchDtoList;
    }
}
