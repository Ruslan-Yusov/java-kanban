package kanban.service;

import kanban.manager.ServerBackedTasksManager;
import kanban.manager.exception.ManagerRestoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KVClient {
    @Value("${application.kvserver.url}")
    private String url;

    private final RestTemplate restTemplate;

    public String get() {
        return execute("load", HttpMethod.GET, Void.class, String.class, null)
                .orElseThrow(ManagerRestoreException::new);
    }

    public void save(String value) {
        execute("save", HttpMethod.POST, String.class, Void.class, value);
    }

    protected <IN, OUT> Optional<OUT> execute(
            String path,
            HttpMethod method,
            Class<IN> inClazz,
            Class<OUT> outClazz,
            IN data
    ) {
        URI uri = URI.create(String.format("%s%s", url, path));
        HttpHeaders httpHeaders = headers("DEBUG");

        HttpEntity<IN> entity = (data != null) ? new HttpEntity<>(data, httpHeaders) : new HttpEntity<>(httpHeaders);
        ResponseEntity<OUT> responseEntity = restTemplate.exchange(uri, method, entity, outClazz);
        return Optional.ofNullable(responseEntity.getBody());
    }

    private HttpHeaders headers(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        httpHeaders.set("API_TOKEN", token);
        return httpHeaders;
    }

    public ServerBackedTasksManager restoreFromServer() {
        try {
            return JsonUtils.MAPPER.readValue(get(), ServerBackedTasksManager.class);
        } catch (IOException e) {
            throw new ManagerRestoreException(e);
        }
    }
}
