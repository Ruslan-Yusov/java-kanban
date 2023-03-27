package kanban.manager;

import kanban.tasks.EpicTask;
import kanban.tasks.Status;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ServiceManager {
    private final Map<Integer, Task> tasks = new TreeMap<>();
    private int currentId = 1;

    public int getNextId() {
        return currentId++;
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public void deleteTask(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            if (task instanceof EpicTask) {
                ((EpicTask) task)
                        .getSubTasks()
                        .stream()
                        .map(Task::getId)
                        .forEach(this.tasks::remove);
            }
            task.onDelete();
            tasks.remove(id);
        }
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException("Неизвестная задача");
        }
    }

    public List<SubTask> getSubTasks(EpicTask epicTask) {
        return (epicTask != null) ? new ArrayList<>(epicTask.getSubTasks()) : Collections.emptyList();
    }

    public Task setTaskStatus(Task task, Status newStatus) {
        if (task != null) {
            task.setStatus(newStatus);
        }
        return task;
    }

    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            addTask(task);
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public String toString() {
        return tasks
                .values()
                .stream()
                .map(Task::toString)
                .collect(Collectors
                        .joining("\n", "задачи:\n", "\n------\n"));
    }
}
