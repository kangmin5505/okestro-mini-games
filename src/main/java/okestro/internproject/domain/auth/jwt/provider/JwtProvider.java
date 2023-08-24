package okestro.internproject.domain.auth.jwt.provider;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.auth.jwt.dto.JwtDto;
import okestro.internproject.domain.auth.jwt.dto.JwtUserDto;
import okestro.internproject.domain.auth.jwt.service.RefreshTokenService;
import okestro.internproject.domain.auth.oauth2.principal.PrincipalDetails;
import okestro.internproject.domain.auth.util.CookieUtils;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class JwtProvider {


    public final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;               //30분
    public final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     //7일
    public static final String TYPE_ACCESS = "omg_.access";
    public static final String TYPE_REFRESH = "omg_.refresh";
    private final Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public JwtProvider(UserService userService, RefreshTokenService refreshTokenService,
                       @Value("${app.jwt.secret}") String secretKey) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Optional<String> resolveToken(HttpServletRequest request, String tokenType) {
        if (request.getCookies() != null) {
            return Arrays
                    .stream(request.getCookies())
                    .filter((cookie) -> cookie.getName().equals(tokenType))
                    .map(Cookie::getValue)
                    .findFirst();
        }
        return Optional.empty();
    }


    public void setAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        UUID userId = getUserIdFromClaims(claims);
        userService.findById(userId)
                .map(PrincipalDetails::new)
                .map(principal -> new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities()))
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
    }

    // FIXME: 중복 제거 (parseClaims, validateToken)
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        UUID userId = getUserIdFromClaims(claims);

        return refreshTokenService.findByUserId(userId)
                .map((refreshToken) -> token.equals(refreshToken.getToken()))
                .orElse(false);
    }

    public JwtDto createTokenDto(SimpleUser user) {
        Date now = new Date();

        String accessToken = createAccessToken(user, now);
        String refreshToken = createRefreshToken(user, now);

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(SimpleUser user, Date now) {
        return createToken(user, now, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(SimpleUser user, Date now) {
        return createToken(user, now, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String createToken(SimpleUser user, Date now, long tokenExpireTime) {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");

        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("userId", user.getId());
        accessClaims.put("email", user.getEmail());
        accessClaims.put("nickname", user.getNickname());

        return Jwts.builder()
                .setHeader(header)
                .addClaims(accessClaims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpireTime))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String reissueAccessToken(ServletResponse response, String refreshToken) {
        String reissuedAccessToken = getNewAccessToken(refreshToken);
        CookieUtils.setTokenCookie((HttpServletResponse) response, reissuedAccessToken, JwtProvider.TYPE_ACCESS);
        return reissuedAccessToken;
    }

    private String getNewAccessToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        JwtUserDto jwtUserDto = getJwtUserFromClaims(claims);

        return createAccessToken(jwtUserDto, new Date());
    }


    public JwtUserDto getJwtUserFromClaims(Claims claims) {
        return JwtUserDto.builder()
                .id(getUserIdFromClaims(claims))
                .email((String) claims.get("email"))
                .nickname((String) claims.get("nickname"))
                .build();
    }

    private UUID getUserIdFromClaims(Claims claims) {
        return UUID.fromString(claims.get("userId", String.class));
    }
}
