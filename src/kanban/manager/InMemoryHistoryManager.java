package kanban.manager;

import kanban.CustomLinkedList;
import kanban.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    public static final int HISTORY_SIZE = 10;
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.link(task);
            if (history.getSize() > HISTORY_SIZE) {
                history.removeFirst();
            }
        }
    }

    @Override
    public void remove (int id) {
        history.removeById(id);
    }

    @Override
    public final List<Task> getHistory() {
       return history.getTasks();
    }
}
