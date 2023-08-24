package okestro.internproject.domain.auth.oauth2.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${app.fe.http-url}")
    private String FE_HTTP_URL;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.error("OAuth2FailureHandler.onAuthenticationFailure");
        // TODO: 인증 실패 페이지 또는 메시지를 보여주도록 구현
        response.sendRedirect(FE_HTTP_URL);
    }
}
