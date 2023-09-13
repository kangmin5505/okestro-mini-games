package okestro.internproject.global.converter;

import okestro.internproject.domain.game.enums.GameTitle;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverter implements Converter<String, GameTitle> {

    @Override
    public GameTitle convert(String source) {
        return GameTitle.valueOf(source.toUpperCase().replace("-", "_"));
    }
}
