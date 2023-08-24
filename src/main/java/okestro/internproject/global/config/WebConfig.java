package okestro.internproject.global.config;

import lombok.RequiredArgsConstructor;
import okestro.internproject.global.converter.StringToEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToEnumConverter stringToEnumConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToEnumConverter);
    }
}
