package okestro.internproject.domain.user.entity.db;

import lombok.*;
import okestro.internproject.domain.auth.oauth2.enums.OAuth2Provider;
import okestro.internproject.domain.auth.oauth2.enums.Role;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.global.entity.BaseDateTimeEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static javax.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "users")
public class User extends BaseDateTimeEntity implements SimpleUser {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String avatarURL;

    @Column(nullable = false)
    @Enumerated(STRING)
    private OAuth2Provider oAuth2Provider;

    @Column(nullable = false)
    @Enumerated(STRING)
    private Role role;

    @Builder
    public User(String email, String nickname, String avatarURL, OAuth2Provider oAuth2Provider, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.avatarURL = avatarURL;
        this.oAuth2Provider = oAuth2Provider;
        this.role = role;
    }
}
