package kanban.manager;

import kanban.tasks.Task;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Managers {
    public static TaskManager<Task, Integer> getDefault(HistoryManager<Task, String> historyManager) {
        return getTaskManager(historyManager);
    }

    public static HistoryManager<Task, String> getDefaultHistory() {
        return getHistoryManager();
    }

    private static TaskManager<Task, Integer> getTaskManager(HistoryManager<Task, String> historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    private static HistoryManager<Task, String> getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
