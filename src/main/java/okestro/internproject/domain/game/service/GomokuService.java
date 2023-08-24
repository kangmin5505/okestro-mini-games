package okestro.internproject.domain.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.repository.stompRepository.GomokuRepository;
import okestro.internproject.domain.user.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GomokuService extends GameStompService {

    private final GomokuRepository gomokuRepository;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public GomokuService(GomokuRepository gomokuRepository,
                         OnlineUserService onlineUserService,
                         SimpMessagingTemplate simpMessagingTemplate,
                         ObjectMapper objectMapper) {
        super(gomokuRepository, onlineUserService, simpMessagingTemplate, objectMapper);
        this.gomokuRepository = gomokuRepository;
        this.onlineUserService = onlineUserService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void startGame(UUID gameRoomId) {

    }

    @Override
    public void finishGame(GameRoom gameRoom, UUID gameRoomId) {

    }
}
