package kanban.manager;

import kanban.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task, String> {
    public static final int HISTORY_SIZE = 10;
    private final LinkedList<String> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == HISTORY_SIZE) {
                history.removeFirst();
            }
            history.add(task.toString());
        }
    }

    @Override
    public final List<String> getHistory() {
        return history;
    }
}
