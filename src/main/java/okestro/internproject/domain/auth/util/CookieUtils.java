package okestro.internproject.domain.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CookieUtils {

    @Value("${app.fe.http-url}")
    private static String FE_HTTP_URL;
    private static final Long MAX_COOKIE_AGE = 7 * 24 * 60 * 60L;


    public static void setTokenCookie(HttpServletResponse response, String token, String tokenType) {
        ResponseCookie accessCookie = createTokenCookie(token, tokenType);
        response.addHeader("Set-Cookie", accessCookie.toString());
    }

    private static ResponseCookie createTokenCookie(String token, String tokenType) {
        return ResponseCookie.from(tokenType, token)
                .domain(FE_HTTP_URL)
                .maxAge(MAX_COOKIE_AGE)
                .path("/")
//                .secure(true)
//                .sameSite("None")
                .httpOnly(true)
                .build();
    }
}
