package kanban.manager;

import java.util.List;

public interface HistoryManager<T, V> {

    void add(T task);

    List<V> getHistory();
}
