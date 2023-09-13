package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Getter;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDto;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDtos;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class CardMatchingInstance implements GameInstance {

    private final SimpleUser player1;
    private final SimpleUser player2;
    private int player1Score = 0;
    private int player2Score = 0;
    private final List<CardItem> cards;
    private final LocalDateTime startTime;
    private LocalDateTime finishTime;
    private UUID turnId = null;
    private UUID firstTurnId = null;
    private int matchedPairCount = 0;

    @Builder
    public CardMatchingInstance(SimpleUser player1, SimpleUser player2) {
        this.player1 = player1;
        this.player2 = player2;

        List<String> cardItems = Arrays.asList("ASM", "C++", "C", "Dart", "Fortran", "Go", "Java", "JavaScript",
                "Kotlin", "LISP", "Perl", "PHP", "Python", "Ruby", "Rust", "Scala", "Swift", "TypeScript");

        this.cards = cardItems.stream()
                .flatMap(card -> Stream.of(
                        CardItem.builder().value(card).visible(false).position(0).matched(false).build(),
                        CardItem.builder().value(card).visible(false).position(0).matched(false).build()
                ))
                .collect(Collectors.toList());

        Collections.shuffle(cards);

        for (int index = 0; index < cards.size(); index++) {
            cards.get(index).setPosition(index);
        }
        startTime = LocalDateTime.now();
    }

    @Override
    public UUID getWinnerId() {
        if (player1Score == player2Score) {
            return firstTurnId == player1.getId() ? player2.getId() : player1.getId();
        }
        return player1Score > player2Score ? player1.getId() : player2.getId();
    }

    @Override
    public void leaveUserOnGame(UUID userId) {
        player1Score = userId.equals(player1.getId()) ? -1 : player1Score;
        player2Score = userId.equals(player2.getId()) ? -1 : player2Score;
    }

    public boolean isFinished() {
        return matchedPairCount == cards.size() / 2;
    }

    public boolean isMatched(CardMatchDtos cardMatchDtos) {
        CardMatchDto cardMatchDto1 = cardMatchDtos.getCard1();
        CardMatchDto cardMatchDto2 = cardMatchDtos.getCard2();

        if (cardMatchDto1.getFaceValue().equals(cardMatchDto2.getFaceValue())) {
            int position1 = cardMatchDto1.getPosition();
            int position2 = cardMatchDto2.getPosition();
            cards.get(position1).setVisible(true);
            cards.get(position2).setVisible(true);
            cards.get(position1).setMatched(true);
            cards.get(position1).setMatched(true);
            matchedPairCount++;
            return true;
        }
        return false;
    }

    public void addScore() {
        player1Score += turnId.equals(player1.getId()) ? 1 : 0;
        player2Score += turnId.equals(player2.getId()) ? 1 : 0;
    }

    public UUID getCurrentTurnId() {
        return this.turnId;
    }

    public UUID changeTurnId() {
        if (turnId == null) {
            turnId = Math.random() < 0.5 ? player1.getId() : player2.getId();
            firstTurnId = turnId;
        } else if (turnId.equals(player1.getId())) {
            turnId = player2.getId();
        } else {
            turnId = player1.getId();
        }
        return this.turnId;
    }

    @Override
    public void startGame() {
    }

    @Override
    public void finishGame() {
        finishTime = LocalDateTime.now();
    }
}
