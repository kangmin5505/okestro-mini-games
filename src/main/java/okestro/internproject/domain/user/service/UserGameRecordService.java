package okestro.internproject.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.user.dto.UserGameRecordDto;
import okestro.internproject.domain.user.dto.UserGameRecordPageDto;
import okestro.internproject.domain.user.dto.UserGameStatsDto;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.repository.UserGameRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserGameRecordService {

    private final UserGameRecordRepository userGameRecordRepository;
    private final UserService userService;


    public UserGameStatsDto getRelativeGameStats(UUID userId, UUID opponentId) {
        if (userId.equals(opponentId)) {
            return UserGameStatsDto.createEmptyUserGameStatsDto();
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        User opponentUser = userService.findById(opponentId)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));

        List<UserGameRecord> userWinRecords = userGameRecordRepository.findAllByWinUserAndLoseUser(user, opponentUser);
        List<UserGameRecord> userLoseRecords = userGameRecordRepository.findAllByWinUserAndLoseUser(opponentUser, user);

        return UserGameStatsDto.createUserGameStatsDto(userWinRecords, userLoseRecords);
    }

    public UserGameRecordPageDto findUserRecordPage(User user, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<UserGameRecord> userRecordPage = userGameRecordRepository.findAllByWinUserOrLoseUser(user, user, pageable);
        long totalElements = userRecordPage.getTotalElements();

        List<UserGameRecordDto> userGameRecordDtos = userRecordPage.stream()
                .map((userGameRecord) ->
                        UserGameRecordDto.builder()
                                .gameTitle(userGameRecord.getGame().getTitle())
                                .winUserNickname(userGameRecord.getWinUser().getNickname())
                                .loseUserNickname(userGameRecord.getLoseUser().getNickname())
                                .winUserScore(userGameRecord.getWinUserScore())
                                .loseUserScore(userGameRecord.getLoseUserScore())
                                .startTime(userGameRecord.getStartTime())
                                .finishTime(userGameRecord.getFinishTime())
                                .build()
                )
                .collect(Collectors.toList());

        return UserGameRecordPageDto.builder()
                .totalElements(totalElements)
                .userGameRecords(userGameRecordDtos)
                .build();
    }

    public UserGameRecord save(UserGameRecord userGameStat) {
        return userGameRecordRepository.save(userGameStat);
    }
}
