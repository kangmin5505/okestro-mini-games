package okestro.internproject.domain.user.entity.db;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.global.entity.BaseDateTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "game_id"})
})
public class UserGameStat extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_game_stat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false)
    private Integer wins;

    @Column(nullable = false)
    private Integer loses;

    public void updateWins() {
        this.wins++;
    }

    public void updateLoses() {
        this.loses++;
    }

    @Builder
    public UserGameStat(User user, Game game, Integer wins, Integer loses) {
        this.user = user;
        this.game = game;
        this.wins = wins;
        this.loses = loses;
    }
}
