package okestro.internproject.global.config;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.filter.JwtAuthenticationFilter;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.auth.jwt.service.RefreshTokenService;
import okestro.internproject.domain.auth.oauth2.handler.OAuth2FailureHandler;
import okestro.internproject.domain.auth.oauth2.handler.OAuth2SuccessHandler;
import okestro.internproject.domain.auth.oauth2.service.OAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserServiceImpl oAuth2UserServiceImpl;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.fe.http-url}")
    private String FE_URL;

    @Bean
    public SecurityFilterChain jwtSecurity(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated();

        http
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oAuth2UserServiceImpl)
                .and()
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler);

        http.
                logout()
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessHandler(this::logoutSuccessHandler)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .deleteCookies(JwtProvider.TYPE_ACCESS)
                .deleteCookies(JwtProvider.TYPE_REFRESH);

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void logoutSuccessHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        jwtProvider.resolveToken(request, JwtProvider.TYPE_REFRESH)
                .map(jwtProvider::parseClaims)
                .map(jwtProvider::getJwtUserFromClaims)
                .ifPresent(user -> {
                    refreshTokenService.deleteByUserId(user.getId());
                });
        response.setStatus(HttpStatus.CREATED.value());
    }
}
