package kanban.manager;

import kanban.tasks.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class InMemoryTaskManager implements TaskManager<Task, Integer> {
    public static final int HISTORY_SIZE = 10;
    private final Map<Integer, Task> tasks = new TreeMap<>();
    private int currentId = 1;
    private final HistoryManager<Task> historyManager;

    public InMemoryTaskManager(HistoryManager<Task> historyManager) {
        this.historyManager = historyManager;
    }

    /**
     * Индентификатор
     *
     * @return возвращает индентификатор
     */

    private int getNextId() {
        return currentId++;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getEpicTask(Integer id) {
        Task task = tasks.get(id);
        if (task instanceof EpicTask) {
            historyManager.add(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Task getSubTask(Integer id) {
        Task task = tasks.get(id);
        if (task instanceof SubTask) {
            historyManager.add(task);
            return task;
        } else {
            return null;
        }
    }

    /**
     * Каскадное удаление задач из списка
     *
     * @param task задача
     */
    private void deleteAnyTask(Task task) {
        if (task != null) {
            if (task instanceof EpicTask) {
                ((EpicTask) task)
                        .getSubTasks()
                        .stream()
                        .map(Task::getId)
                        .forEach(this.tasks::remove);
            }
            task.onDelete();
            tasks.remove(task.getId());
            historyManager.remove(task.getId());
        }
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = tasks.get(id);
        if (task.getClass().equals(Task.class)) {
            deleteAnyTask(task);
        }
    }

    @Override
    public void deleteSubTask(Integer id) {
        Task task = tasks.get(id);
        if (task instanceof SubTask) {
            deleteAnyTask(task);
        }
    }

    @Override
    public void deleteEpicTask(Integer id) {
        Task task = tasks.get(id);
        if (task instanceof EpicTask) {
            deleteAnyTask(task);
        }
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearTasks() {
        tasks.values().stream().filter(t -> Task.class.equals(t.getClass())).forEach(this::deleteAnyTask);
    }

    @Override
    public void clearSubTasks() {
        tasks.values().stream().filter(SubTask.class::isInstance).forEach(this::deleteAnyTask);
    }

    @Override
    public void clearEpicTasks() {
        tasks.values().stream().filter(EpicTask.class::isInstance).forEach(this::deleteAnyTask);
    }

    @Override
    public void addTask(Task task) {
        int index = getNextId();
        task.setId(index);
        tasks.put(index, task);
    }

    @Override
    public <E extends Task> void addEpicTask(E epicTask) {
        addTask(epicTask);
    }

    @Override
    public <S extends Task> void addSubTask(S subTask) {
        addTask(subTask);
    }


    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throwError();
        }
    }

    private static void throwError() {
        throw new IllegalArgumentException("Неизвестная задача");
    }

    @Override
    public <E extends Task> void updateEpicTask(E epicTask) {
        if (tasks.containsKey(epicTask.getId())) {
            tasks.put(epicTask.getId(), epicTask);
        } else {
            throwError();
        }
    }

    @Override
    public <S extends Task> void updateSubTask(S subTask) {
        if (tasks.containsKey(subTask.getId())) {
            tasks.put(subTask.getId(), subTask);
        } else {
            throwError();
        }
    }

    @Override
    public <E extends ParentTask<? extends Task>> List<SubTask> getSubTasks(E epicTask) {
        return Optional.ofNullable(epicTask)
                .map(ParentTask::getSubTasks)
                .map(set -> set.stream().map(SubTask.class::cast).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    @Override
    public <A extends Task> A setTaskStatus(A task, Status newStatus) {
        if (task != null) {
            task.setStatus(newStatus);
        }
        return task;
    }

    @Override
    public final void addTasks(Task... tasks) {
        for (Task task : tasks) {
            addTask(task);
        }
    }

    @Override
    public List<? extends Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
