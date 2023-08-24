package okestro.internproject.domain.auth.oauth2.provider;

import okestro.internproject.domain.auth.oauth2.enums.OAuth2Provider;

public interface OAuth2UserInfo {
    OAuth2Provider getProvider();

    String getEmail();

    String getName();
}
