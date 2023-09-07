package okestro.internproject.domain.auth.oauth2.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.oauth2.enums.Role;
import okestro.internproject.domain.auth.oauth2.principal.PrincipalDetails;
import okestro.internproject.domain.auth.oauth2.provider.GoogleUserInfo;
import okestro.internproject.domain.auth.oauth2.provider.OAuth2UserInfo;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserService userService;
    private final UserGameStatService userGameRecordService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        User user = userService.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(oAuth2UserInfo.getEmail())
                            .nickname(oAuth2UserInfo.getName())
                            .oAuth2Provider(oAuth2UserInfo.getProvider())
                            .role(Role.USER)
                            .build();
                    userService.save(newUser);
                    userGameRecordService.create(newUser);
                    return newUser;
                });

        return new PrincipalDetails(user);
    }
}
