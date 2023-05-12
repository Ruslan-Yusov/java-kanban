package kanban.manager;

import kanban.CustomLinkedList;
import kanban.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.link(task);
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
