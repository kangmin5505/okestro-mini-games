package okestro.internproject.domain.user.entity;

import java.util.UUID;

public interface SimpleUser {
    UUID getId();
    String getEmail();
    String getNickname();
}
