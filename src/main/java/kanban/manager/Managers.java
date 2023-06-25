package kanban.manager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import kanban.manager.tasks.Task;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Managers {

    public static final String DEFAULT_TASK_MANAGER_FILE = "tasks.json";

    public static TaskManager<Task, Integer> getDefault(HistoryManager<Task> historyManager) {
        return getFileTaskManager(historyManager);
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return getHistoryManager();
    }

    public static TaskManager<Task, Integer> getInMemoryTaskManager(HistoryManager<Task> historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static TaskManager<Task, Integer> getServerBackedTasksManager() {
        return new ServerBackedTasksManager();
    }

    public static TaskManager<Task, Integer> getFileTaskManager(HistoryManager<Task> historyManager) {
        return new FileBackedTasksManager(historyManager, DEFAULT_TASK_MANAGER_FILE);
    }

    private static HistoryManager<Task> getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
