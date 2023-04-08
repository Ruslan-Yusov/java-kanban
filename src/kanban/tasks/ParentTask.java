package kanban.tasks;

import java.util.Set;

/**
 * Родительская задача.
 * @param <S> тип подзадач
 */
public interface ParentTask<S extends Task> {
    <T extends S> Set<T> getSubTasks();
}
