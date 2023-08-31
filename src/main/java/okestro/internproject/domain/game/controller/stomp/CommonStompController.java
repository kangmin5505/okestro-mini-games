package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.common.ChatDto;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.service.CardMatchingService;
import okestro.internproject.domain.game.service.GomokuService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommonStompController {

    private final CardMatchingService cardMatchingService;
    private final GomokuService gomokuService;


    @MessageMapping("/chat/{gameTitle}/{gameRoomId}")
    public ChatDto chatGameRoom(ChatDto chatDto) {
        return chatDto;
    }


    @MessageMapping("/room-maintain/{gameTitle}/{gameRoomId}")
    public String roomMaintain(String message) {
        return message;
    }

    @MessageMapping("/ready-toggle/{gameTitle}/{gameRoomId}")
    public String readyToggle(@DestinationVariable String gameTitle, @DestinationVariable UUID gameRoomId,
                              @Payload Map<String, Object> simpleUser) {
        try {
            switch (gameTitle) {
                case "card-matching":
                    GameRoom cardMatchingRoom = cardMatchingService.findById(gameRoomId)
                            .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
                    cardMatchingRoom.getGameRoomInfo().readyToggle(UUID.fromString(simpleUser.get("id").toString()));
                    break;
                case "gomoku":
                    GameRoom gomokuRoom = gomokuService.findById(gameRoomId)
                            .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
                    gomokuRoom.getGameRoomInfo().readyToggle(UUID.fromString(simpleUser.get("id").toString()));

            }

        } catch (ClassCastException e) {
            log.error("readyToggle error : {}", e.getMessage());
        }
        return simpleUser.toString();
    }


    //    @MessageMapping("/message/{id}")
//    public String handle(Message message, MessageHeaders messageHeaders,
//                         MessageHeaderAccessor messageHeaderAccessor, SimpMessageHeaderAccessor simpMessageHeaderAccessor,
//                         StompHeaderAccessor stompHeaderAccessor, @Payload String payload,
//                         @Header("destination") String destination, @Headers Map<String, String> headers,
//                         @DestinationVariable String id, Principal principal) {
//
//        System.out.println("---- Message ----");
//        System.out.println(message);
//
//        System.out.println("---- MessageHeaders ----");
//        System.out.println(messageHeaders);
//
//        System.out.println("---- MessageHeaderAccessor ----");
//        System.out.println(messageHeaderAccessor);
//
//        System.out.println("---- SimpMessageHeaderAccessor ----");
//        System.out.println(simpMessageHeaderAccessor);
//
//        System.out.println("---- StompHeaderAccessor ----");
//        System.out.println(stompHeaderAccessor);
//
//        System.out.println("---- @Payload ----");
//        System.out.println(payload);
//
//        System.out.println("---- @Header(\"destination\") ----");
//        System.out.println(destination);
//
//        System.out.println("----  @Headers ----");
//        System.out.println(headers);
//
//        System.out.println("----  @DestinationVariable ----");
//        System.out.println(id);
//
//        System.out.println("----  @Principal ----");
//        System.out.println((principal.getName()));
//        System.out.println(((PrincipalDetails)principal).getUser().getNickname());
//        System.out.println(((PrincipalDetails)principal).getUser().getEmail());
//        System.out.println(((PrincipalDetails)principal).getUser().getId());
//        System.out.println(principal);
//
//        return payload;
//    }
//
    @MessageExceptionHandler
    public Exception handleException(Exception exception) {
        log.error(exception.getMessage());
        return exception;
    }
}
