package okestro.internproject.domain.auth.oauth2.provider;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.oauth2.enums.OAuth2Provider;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return extractNameFromEmail(getEmail());
    }

    private String extractNameFromEmail(String email) {
        return email.split("@")[0];
    }
}
