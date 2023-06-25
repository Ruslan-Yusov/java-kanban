package kanban.manager;

import kanban.manager.utils.CustomLinkedList;
import kanban.manager.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        history.link(task);
    }

    @Override
    public void remove(int id) {
        history.removeById(id);
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
    public final List<Task> getHistory() {
        return history.getTasks();
    }
}
