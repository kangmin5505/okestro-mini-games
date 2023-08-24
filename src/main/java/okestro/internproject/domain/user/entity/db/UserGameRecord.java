package okestro.internproject.domain.user.entity.db;

import lombok.*;
import okestro.internproject.domain.game.entity.db.Game;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class UserGameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_game_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "win_user_id")
    private User winUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lose_user_id")
    private User loseUser;

    @Column(nullable = false)
    private Integer winUserScore;

    @Column(nullable = false)
    private Integer loseUserScore;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime finishTime;

    @Builder
    public UserGameRecord(Game game, User winUser, User loseUser, Integer winUserScore, Integer loseUserScore, LocalDateTime startTime, LocalDateTime finishTime) {
        this.game = game;
        this.winUser = winUser;
        this.loseUser = loseUser;
        this.winUserScore = winUserScore;
        this.loseUserScore = loseUserScore;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
