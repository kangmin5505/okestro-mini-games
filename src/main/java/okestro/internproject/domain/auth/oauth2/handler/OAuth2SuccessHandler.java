package okestro.internproject.domain.auth.oauth2.handler;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.dto.JwtDto;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.auth.jwt.service.RefreshTokenService;
import okestro.internproject.domain.auth.oauth2.principal.PrincipalDetails;
import okestro.internproject.domain.auth.util.CookieUtils;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final OnlineUserService onlineUserService;

    @Value("${app.fe.http-url}")
    private String FE_HTTP_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SimpleUser user = getUser(authentication);

        if (onlineUserService.isOnlineUser(user.getId())) {
            response.sendRedirect(FE_HTTP_URL + "/already-login");
            return;
        }

        JwtDto jwt = jwtProvider.createTokenDto(user);
        CookieUtils.setTokenCookie(response, jwt.getAccessToken(), JwtProvider.TYPE_ACCESS);
        CookieUtils.setTokenCookie(response, jwt.getRefreshToken(), JwtProvider.TYPE_REFRESH);

        refreshTokenService.storeRefreshToken(jwt.getRefreshToken(), user.getId());

        response.sendRedirect(FE_HTTP_URL + "/games");
    }

    private SimpleUser getUser(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return principal.getUser();
    }


}
