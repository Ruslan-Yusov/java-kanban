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

    /**
     * Индентификатор
     * @return возвращает индентификатор
     */
    private int getNextId() {
        return currentId++;
    }

    /**
     * Поиск по id в {@link Task}
     * @param id индентификатор
     * @return объект {@link Task}
     */
    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    /**
     * Поиск по id в {@link EpicTask}
     * @param id индентификатор
     * @return объект {@link EpicTask}
     */
    public Task getEpicTask(Integer id) {
        return (getTask(id) instanceof EpicTask) ? getTask(id) : null;
    }

    /**
     * Поиск по id в {@link SubTask}
     * @param id индентификатор
     * @return объект {@link SubTask}
     */
    public Task getSubTask(Integer id) {
        return (getTask(id) instanceof SubTask) ? getTask(id) : null;
    }

    /**
     * Удаление по индентификатору
     * @param id индентификатор
     */
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

    /**
     * Удаление всех задач ({@link Task}, {@link EpicTask}, {@link SubTask})
     */
    public void clearAllTasks() {
        tasks.clear();
    }

    /**
     * Добавление задачи
     * @param task объект типа {@link Task}
     */
    public void addTask(Task task) {
        int index = getNextId();
        task.setId(index);
        tasks.put(index, task);
    }

    /**
     * Добавление эпика
     * @param epicTask объект типа {@link EpicTask}
     */
    public void addEpicTask(EpicTask epicTask) {
        addTask(epicTask);
    }

    /**
     * Добавление подзадачи
     * @param subTask объект типа {@link SubTask}
     */
    public void addSubTask(SubTask subTask) {
        addTask(subTask);
    }

    /**
     * Обновление задачи
     * @param task объект {@link Task}
     */
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException("Неизвестная задача");
        }
    }

    /**
     * Обновление Эпика
     * @param epicTask объект {@link EpicTask}
     */
    public void updateEpicTask(EpicTask epicTask) {
        if (tasks.containsKey(epicTask.getId())) {
            tasks.put(epicTask.getId(), epicTask);
        } else {
            throw new IllegalArgumentException("Неизвестная задача");
        }
    }

    /**
     * Обновление подзадачи
     * @param subTask объект {@link SubTask}
     */
    public void updateSubTask(SubTask subTask) {
        if (tasks.containsKey(subTask.getId())) {
            tasks.put(subTask.getId(), subTask);
        } else {
            throw new IllegalArgumentException("Неизвестная задача");
        }
    }

    /**
     * Получение всех подзадач в рамках одного эпика
     * @param epicTask объект типа {@link EpicTask}
     * @return список всех подзадач
     */
    public List<SubTask> getSubTasks(EpicTask epicTask) {
        return (epicTask != null) ? new ArrayList<>(epicTask.getSubTasks()) : Collections.emptyList();
    }

    /**
     * Установление статусов
     * @param task объект типа {@link Task}
     * @param newStatus новый статус из класса {@link Status}
     */
    public Task setTaskStatus(Task task, Status newStatus) {
        if (task != null) {
            task.setStatus(newStatus);
        }
        return task;
    }

    /**
     * Добавление задач
     * @param tasks объект типа {@link Task}
     */
    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            addTask(task);
        }
    }

    /**
     * Получение всех задач
     * @return список всех задач
     */
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
