package kanban.manager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kanban.manager.exception.ManagerRestoreException;
import kanban.manager.exception.ManagerSaveException;
import kanban.manager.tasks.EpicTask;
import kanban.manager.tasks.SubTask;
import kanban.manager.tasks.Task;
import kanban.service.JsonUtils;
import lombok.Setter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    @Setter
    private String filePath;

    public FileBackedTasksManager(HistoryManager<Task> historyManager, String filePath) {
        // Если менеджер должен быть восстановлен из файла, то использовать статический метод вместо конструктора
        super(historyManager);
        this.filePath = filePath;
        validateFilePath();
    }

    @JsonCreator
    public FileBackedTasksManager(
            @JsonProperty("tasks")
            @JsonDeserialize(as = ArrayList.class, contentAs = Task.class)
            List<Task> tasks,
            @JsonProperty("history")
            List<Integer> historyIds
    ) {
        // Конструктор для десериализации
        super(new InMemoryHistoryManager());
        // Цикличность ссылок разорвана для сериализации:
        // * со стороны SubTask не ссылка на эпик, а EpicTaskId
        // * со стороны EpicTask нет ссылок на подзадачи
        if (tasks != null) {
            tasks.forEach(task -> {
                this.tryAddTaskByStart(task); // can occure runtime error!
                this.tasks.put(task.getId(), task);
            });
        }
        // Для консистентность надо восстановить связи между эпиками и подзадачами
        this.tasks.values().stream()
                .filter(SubTask.class::isInstance)
                .map(SubTask.class::cast)
                .forEach(subTask -> subTask.setEpicTask((EpicTask) this.getEpicTask(subTask.getEpicTaskId())));
        this.historyManager.clear();
        if (historyIds != null) {
            historyIds.stream().map(id -> this.tasks.get(id)).filter(Objects::nonNull).forEach(this.historyManager::add);
        }
        // Инициализация значения счетчика.
        // В постановке нет про восстановление счетчика. Сделано по требованию ревью.
        this.currentId = this.tasks.keySet().stream().max(Comparator.naturalOrder()).orElse(1);
    }

    protected void validateFilePath() {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Не задано имя файла");
        }
        if (Files.notExists(Path.of(filePath))) {
            throw new IllegalArgumentException("Нет доступа к файлу");
        }
    }

    private void saveState() {
        try {
            String data = JsonUtils.MAPPER.writeValueAsString(this);
            saveAsString(data);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    protected void saveAsString(String data) {
        // Здесь файл не создается он должен присутствовать хотя бы пустой
        try (FileOutputStream file = new FileOutputStream(filePath);
             FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)
        ) {
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
                throw new ManagerRestoreException();
            }
            return JsonUtils.MAPPER.readValue(data, FileBackedTasksManager.class);
        } catch (IOException e) {
            throw new ManagerRestoreException(e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        saveState();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        saveState();
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        saveState();
    }

    @Override
    public void deleteEpicTask(Integer id) {
        super.deleteEpicTask(id);
        saveState();
    }

    @Override
    public void clearAllTasks() {
        saveState();
        super.clearAllTasks();
        saveState();
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

}
