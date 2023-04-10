package kanban.manager;

import kanban.tasks.Task;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Managers {
    public static TaskManager<Task, Integer> getDefault(HistoryManager<Task> historyManager) {
        return getTaskManager(historyManager);
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return getHistoryManager();
    }

    private static TaskManager<Task, Integer> getTaskManager(HistoryManager<Task> historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    private static HistoryManager<Task> getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
