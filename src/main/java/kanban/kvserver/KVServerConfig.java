package kanban.kvserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class KVServerConfig {
    @Bean
    public KVServer getServer() {
        KVServer server = null;
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            log.error("Cant start KVServer !!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return server;
    }
}
