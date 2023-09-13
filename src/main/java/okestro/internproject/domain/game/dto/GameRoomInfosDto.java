package okestro.internproject.domain.game.dto;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.game.entity.memory.GameRoom;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameRoomInfosDto {
    private String title;
    private List<GameRoomInfoDto> gameRooms;

    @Builder
    public GameRoomInfosDto(String title, List<GameRoomInfoDto> gameRoomInfoDtos) {
        this.title = title;
        this.gameRooms = gameRoomInfoDtos;
    }

    public static GameRoomInfosDto createGameRoomInfosDto(String gameTitle, List<GameRoom> gameRooms) {
        List<GameRoomInfoDto> gameRoomInfoDtoList = gameRooms.stream()
                .map(GameRoomInfoDto::createGameRoomInfoDto)
                .collect(Collectors.toList());

        return GameRoomInfosDto.builder()
                .title(gameTitle)
                .gameRoomInfoDtos(gameRoomInfoDtoList)
                .build();
    }
}
