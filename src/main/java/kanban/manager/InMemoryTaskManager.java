package kanban.manager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kanban.manager.exception.IntersectedTaskException;
import kanban.manager.exception.UnknownTaskException;
import kanban.manager.tasks.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class InMemoryTaskManager implements TaskManager<Task, Integer> {
    protected Map<Integer, Task> tasks = new TreeMap<>();
    protected Map<LocalDateTime, Task> oneMoreMapByStart = new TreeMap<>();
    @JsonIgnore
    protected int currentId;
    protected HistoryManager<Task> historyManager;

    public InMemoryTaskManager(HistoryManager<Task> historyManager) {
        this.historyManager = historyManager;
    }

    /**
     * Индентификатор.
     *
     * @return возвращает индентификатор
     */

    private int getNextId() {
        return ++currentId;
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
     * Каскадное удаление задач из списка.
     *
     * @param task задача
     */
    private void deleteAnyTask(Task task) {
        if (task != null) {
            if (task instanceof EpicTask) {
                ((EpicTask) task)
                        .getSubTasks()
                        .forEach(t -> {
                            this.tryRemoveTaskByStart(t);
                            this.tasks.remove(t.getId());
                        });
            }
            task.onDelete();
            tryRemoveTaskByStart(task);
            tasks.remove(task.getId());
            historyManager.remove(task.getId());
        }
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = tasks.get(id);
        if (task != null && task.getClass().equals(Task.class)) {
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
        List<Task> toDeleteList = tasks.values().stream()
                .filter(t -> Task.class.equals(t.getClass()))
                .collect(Collectors.toList());
        toDeleteList.forEach(this::deleteAnyTask);
    }

    @Override
    public void clearSubTasks() {
        List<Task> toDeleteList = tasks.values().stream()
                .filter(SubTask.class::isInstance)
                .collect(Collectors.toList());
        toDeleteList.forEach(this::deleteAnyTask);
    }

    @Override
    public void clearEpicTasks() {
        List<Task> toDeleteList = tasks.values().stream()
                .filter(EpicTask.class::isInstance)
                .collect(Collectors.toList());
        toDeleteList.forEach(this::deleteAnyTask);
    }

    @Override
    public void addTask(Task task) {
        int index = getNextId();
        task.setId(index);
        tryAddTaskByStart(task);
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
            tryRemoveTaskByStart(tasks.get(task.getId()));
            tryAddTaskByStart(task);
            tasks.put(task.getId(), task);
        } else {
            throw new UnknownTaskException();
        }
    }

    @Override
    public <E extends Task> void updateEpicTask(E epicTask) {
        if (tasks.containsKey(epicTask.getId())) {
            tryRemoveTaskByStart(tasks.get(epicTask.getId()));
            tryAddTaskByStart(epicTask);
            tasks.put(epicTask.getId(), epicTask);
        } else {
            throw new UnknownTaskException();
        }
    }

    @Override
    public <S extends Task> void updateSubTask(S subTask) {
        if (tasks.containsKey(subTask.getId())) {
            tryRemoveTaskByStart(tasks.get(subTask.getId()));
            tryAddTaskByStart(subTask);
            tasks.put(subTask.getId(), subTask);
        } else {
            throw new UnknownTaskException();
        }
    }

    @Override
    public <E extends ParentTask<? extends Task>> List<SubTask> getSubTasks(E epicTask) {
        return ofNullable(epicTask)
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
    @JsonIgnore
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(oneMoreMapByStart.values());
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

    protected void tryAddTaskByStart(Task task) {
        LocalDateTime startTime = task.getStartTime();
        if (startTime != null) {
            // не добавлять в список по датам
            Task foundTask = oneMoreMapByStart.get(startTime);
            if (foundTask == null) {
                if (task instanceof EpicTask) {
                    // ignore
                    // Sonar не прав, удалять нельзя.
                } else {
                    checkIntersections(task);
                    oneMoreMapByStart.put(startTime, task);
                }
            } else {
                throw new IntersectedTaskException();
            }
        }
    }

    private void checkIntersections(Task task) {
        for (Task t: getAllTasks()) {
            if (!(t instanceof EpicTask) && checkIntersection(task, t)) {
                throw new IntersectedTaskException();
            }
        }
    }

    private boolean checkIntersection(Task task, Task otherTask) {
        return Math.max(
                ofNullable(task.getStartTime()).orElse(LocalDateTime.MIN)
                        .toInstant(ZoneOffset.UTC).getEpochSecond(),
                ofNullable(otherTask.getStartTime()).orElse(LocalDateTime.MIN)
                        .toInstant(ZoneOffset.UTC).getEpochSecond()
        ) < Math.min(
                ofNullable(task.getEndTime()).orElse(LocalDateTime.MIN)
                        .toInstant(ZoneOffset.UTC).getEpochSecond(),
                ofNullable(otherTask.getEndTime()).orElse(LocalDateTime.MIN)
                        .toInstant(ZoneOffset.UTC).getEpochSecond()
        );
    }

    protected void tryRemoveTaskByStart(Task task) {
        ofNullable(task.getStartTime()).ifPresent(oneMoreMapByStart::remove);
    }
}
