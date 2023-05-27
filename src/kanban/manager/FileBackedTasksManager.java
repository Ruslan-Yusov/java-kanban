package kanban.manager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import kanban.tasks.EpicTask;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String filePath;
    public static final ObjectMapper mapper = jsonMapper();

    public FileBackedTasksManager(HistoryManager<Task> historyManager, String filePath) {
        // Если менеджер должен быть восстановлен из файла, то использовать статический метод вместо конструктора
        super(historyManager);
        this.filePath = filePath;
        validateFilePath();
    }

    private void validateFilePath() {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Не задано имя файла");
        }
        if (Files.notExists(Path.of(filePath))) {
            throw new IllegalArgumentException("Нет доступа к файлу");
        }
    }

    private void saveToFile() {
        // Здесь файл не создается он должен присутствовать хотя бы пустой
        try (FileOutputStream file = new FileOutputStream(filePath);
             FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)
        ) {
            String data = mapper.writeValueAsString(this);
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTasksManager restoreFromFile(String filePath) {
        // Здесь файл не создается он должен присутствовать хотя бы пустой
        try {
            String data = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            if (data.isBlank()) {
                throw new ManagerRestoreException(null);
            }
            return mapper.readValue(data, FileBackedTasksManager.class);
        } catch (IOException e) {
            throw new ManagerRestoreException(e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        saveToFile();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        saveToFile();
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        saveToFile();
    }

    @Override
    public void deleteEpicTask(Integer id) {
        super.deleteEpicTask(id);
        saveToFile();
    }

    @Override
    public void clearAllTasks() {
        saveToFile();
        super.clearAllTasks();
        saveToFile();
    }

    @JsonProperty("history")
    public List<Integer> getHistoryIds() {
        return super.getHistory().stream().map(Task::getId).collect(Collectors.toList());
    }

    @JsonProperty("tasks")
    @Override
    public List<? extends Task> getAllTasks() {
        return super.getAllTasks();
    }

    @JsonCreator
    public FileBackedTasksManager(
            @JsonProperty("tasks") TaskList tasks,
            @JsonProperty("history") TaskIdList historyIds
    ) {
        // Конструктор для десериализации
        super(new InMemoryHistoryManager());
        // Цикличность ссылок разорвана для сериализации:
        // * со стороны SubTask не ссылка на эпик, а EpicTaskId
        // * со стороны EpicTask нет ссылок на подзадачи
        if (tasks != null) {
            tasks.forEach(task -> this.tasks.put(task.getId(), task));
        }
        // Для консистентности надо восстановить связи между эпиками и подзадачами
        this.tasks.values().stream()
                .filter(SubTask.class::isInstance)
                .map(SubTask.class::cast)
                .forEach(subTask -> subTask.setEpicTask((EpicTask) this.getEpicTask(subTask.getEpicTaskId())));
        this.historyManager.clear();
        if (historyIds != null) {
            historyIds.stream().map(id -> this.tasks.get(id)).filter(Objects::nonNull).forEach(this.historyManager::add);
        }
    }


    private static ObjectMapper jsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    private static class TaskList extends ArrayList<Task> {
    }

    private static class TaskIdList extends ArrayList<Integer> {
    }
}
