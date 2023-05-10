package kanban.manager;

import java.util.List;

public interface HistoryManager<T> {

    void add(T task);

    List<T> getHistory();

    void remove (int id);
}
