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

    @Column(nullable = false)
    private Integer totalGames;

    @Column(nullable = false)
    private Integer winPercentage;

    public void updateWins() {
        this.wins++;
        update();
    }

    public void updateLoses() {
        this.loses++;
        update();
    }

    private void update() {
        this.totalGames++;
        this.winPercentage = (int) Math.round((double) this.wins / this.totalGames * 100);
    }

    @Builder
    public UserGameStat(User user, Game game, Integer wins, Integer loses, Integer totalGames, Integer winPercentage) {
        this.user = user;
        this.game = game;
        this.wins = wins;
        this.loses = loses;
        this.totalGames = totalGames;
        this.winPercentage = winPercentage;
    }
}
