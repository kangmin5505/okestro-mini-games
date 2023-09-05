package okestro.internproject.global.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import okestro.internproject.domain.user.service.OnlineUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomPrometheusConfig {

    @Bean
    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }

    @Bean
    public MeterBinder onlineUserMeterBinder(OnlineUserService onlineUserService) {
        return registry ->
                Gauge.builder("my.user.online", onlineUserService, OnlineUserService::getOnlineUserCount)
                        .register(registry);
    }
}
