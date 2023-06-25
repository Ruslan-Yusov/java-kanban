package kanban.manager.tasks;

import java.util.Set;

/**
 * Родительская задача.
 * @param <T> тип подзадач
 */
public interface ParentTask<T extends Task> {
    Set<T> getSubTasks();
}
