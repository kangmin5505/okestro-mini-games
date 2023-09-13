package okestro.internproject.domain.game.enums;

public enum CardMatchingMessageType implements MessageType {
    INIT_BOARD,
    FINISH_GAME,
    MATCHED_CARD,
    SCORE_CHANGE,
    TURN_CHANGE
}
