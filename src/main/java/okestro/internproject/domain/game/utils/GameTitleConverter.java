package okestro.internproject.domain.game.utils;

import okestro.internproject.domain.game.enums.GameTitle;

import javax.persistence.AttributeConverter;

public class GameTitleConverter implements AttributeConverter<GameTitle, String> {
    @Override
    public String convertToDatabaseColumn(GameTitle attribute) {
        return attribute.getTitle();
    }

    @Override
    public GameTitle convertToEntityAttribute(String dbData) {
        return GameTitle.valueOf(dbData.toUpperCase());
    }
}
