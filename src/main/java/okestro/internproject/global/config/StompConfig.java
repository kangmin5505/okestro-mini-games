package okestro.internproject.global.config;

import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.auth.oauth2.principal.PrincipalDetails;
import okestro.internproject.domain.game.service.CardMatchingService;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final OnlineUserService onlineUserService;
    private final CardMatchingService cardMatchingService;


    @Autowired
    public StompConfig(OnlineUserService onlineUserService, @Lazy CardMatchingService cardMatchingService) {
        this.onlineUserService = onlineUserService;
        this.cardMatchingService = cardMatchingService;
    }

    @Value("${app.fe.http-url}")
    private String FE_HTTP_URL;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns(FE_HTTP_URL);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                PrincipalDetails principalDetails = principalToPrincipalDetails(accessor.getUser());

                switch (Objects.requireNonNull(accessor.getCommand())) {
                    case CONNECT:
                        onlineUserService.addOnlineUser(principalDetails.getUser());
                        break;
                    case DISCONNECT:
                        removeOnlineUser(principalDetails.getUser());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void removeOnlineUser(SimpleUser user) {
        OnlineUser onlineUser = onlineUserService.findByUserId(user.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));

        String gameTitle = onlineUser.getGameTitle();
        UUID gameRoomId = onlineUser.getGameRoomId();
        if (gameTitle == null || gameRoomId == null) {
            onlineUserService.removeOnlineUser(user);
            return;
        }

        switch (gameTitle) {
            case "card-matching":
                cardMatchingService.deleteGameRoom(gameRoomId, user);
                break;
            default:
                break;
        }
        onlineUserService.removeOnlineUser(user);
    }

    private PrincipalDetails principalToPrincipalDetails(Principal principal) {
        return (PrincipalDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }
}


