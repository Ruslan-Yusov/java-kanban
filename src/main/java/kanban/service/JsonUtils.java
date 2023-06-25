package kanban.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static final ObjectMapper MAPPER = jsonMapper();
    private static ObjectMapper jsonMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
    objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    objectMapper.findAndRegisterModules();
    return objectMapper;
}
}
