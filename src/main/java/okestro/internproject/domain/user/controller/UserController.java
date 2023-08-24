package okestro.internproject.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.user.dto.UserDetailDto;
import okestro.internproject.domain.user.dto.UserDto;
import okestro.internproject.domain.user.dto.UserGameRecordPageDto;
import okestro.internproject.domain.user.dto.UserGameStatsDto;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserGameStatService userGameStatService;
    private final UserGameRecordService userGameRecordService;

    @GetMapping("/self-info")
    public UserDto userSelfInfo(
            @CookieValue(name = JwtProvider.TYPE_ACCESS) SimpleUser user
    ) {
        return UserDto.createUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDetailDto getUserDetail(@PathVariable UUID userId) {
        return userService.findById(userId)
                .map(UserDetailDto::createUserDetailDto)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
    }

    @GetMapping("/{userId}/game-records")
    public UserGameRecordPageDto getUserGameRecords(@PathVariable UUID userId,
                                                    @RequestParam(defaultValue = "0", required = false) Integer page,
                                                    @RequestParam(defaultValue = "10", required = false) Integer limit) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        return userGameRecordService.findUserRecordPage(user, page, limit);
    }

    @GetMapping("/{userId}/relative-game-stats")
    public UserGameStatsDto getRelativeGameStats(@PathVariable UUID userId,
                                                 @CookieValue(name = JwtProvider.TYPE_ACCESS) SimpleUser user) {
        return userGameRecordService.getRelativeGameStats(user.getId(), userId);
    }

    @GetMapping("/{userId}/game-stats")
    public UserGameStatsDto getUserGameStats(@PathVariable UUID userId) {
        return userGameStatService.findAllByUserId(userId)
                .map(UserGameStatsDto::createUserGameStatsDto)
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
    }


}
