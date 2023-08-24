package okestro.internproject.domain.auth.jwt.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.global.entity.BaseDateTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @NotBlank
    @Column(length = 512)
    private String token;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public void update(String token) {
        this.token = token;
    }
}
