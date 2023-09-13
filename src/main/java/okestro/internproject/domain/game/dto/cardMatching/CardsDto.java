package okestro.internproject.domain.game.dto.cardMatching;

import lombok.AllArgsConstructor;
import lombok.Data;
import okestro.internproject.domain.game.entity.memory.CardItem;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CardsDto {

    private List<CardDto> cards;

    public static CardsDto createCardsDto(List<CardItem> cardItems) {

        List<CardDto> cardDtoList = cardItems.stream()
                .map((card) ->
                        CardDto.builder()
                                .value(card.getValue())
                                .visible(card.isVisible())
                                .position(card.getPosition())
                                .matched(card.isMatched())
                                .build()
                )
                .collect(Collectors.toList());

        return new CardsDto(cardDtoList);
    }
}
