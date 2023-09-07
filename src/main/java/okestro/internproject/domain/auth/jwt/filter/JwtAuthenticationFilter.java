package okestro.internproject.domain.auth.jwt.filter;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Optional<String> accessTokenOptional = jwtProvider.resolveToken((HttpServletRequest) request, JwtProvider.TYPE_ACCESS);

        if (accessTokenOptional.isPresent() && jwtProvider.validateToken(accessTokenOptional.get())) {
            jwtProvider.setAuthentication(accessTokenOptional.get());
        } else {
            jwtProvider.resolveToken((HttpServletRequest) request, JwtProvider.TYPE_REFRESH)
                    .ifPresent((refreshToken) -> reissueAccessTokenIfRefreshTokenIsValid(response, refreshToken));
        }
        chain.doFilter(request, response);
    }

    private void reissueAccessTokenIfRefreshTokenIsValid(ServletResponse response, String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String reissuedAccessToken = jwtProvider.reissueAccessToken(response, refreshToken);
            jwtProvider.setAuthentication(reissuedAccessToken);
        }
    }
}
