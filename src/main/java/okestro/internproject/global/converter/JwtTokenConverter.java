package okestro.internproject.global.converter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.user.entity.SimpleUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenConverter implements Converter<String, SimpleUser> {

    private final JwtProvider jwtProvider;

    @Override
    public SimpleUser convert(String source) {
        Claims claims = jwtProvider.parseClaims(source);
        return jwtProvider.getJwtUserFromClaims(claims);
    }
}
