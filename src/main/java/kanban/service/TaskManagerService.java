package kanban.service;

import kanban.manager.TaskManager;
import kanban.manager.tasks.*;
import kanban.service.dto.TaskDto;
import kanban.service.dto.TaskDtoForCreate;
import kanban.service.dto.TaskListDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskManagerService {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    private TaskManager<Task, Integer> taskManager;

    public TaskListDto getTasks() {
        List<? extends Task> allTasks = taskManager.getAllTasks();
        return taskListToTaskListDto(allTasks);
    }

    TaskDto getTask(Integer id) {
        return mapper.toTaskDto(taskManager.getTask(id));
    }

    TaskDto getEpicTask(Integer id) {
        return mapper.toTaskDto(taskManager.getEpicTask(id));
    }

    TaskDto getSubTask(Integer id) {
        return mapper.toTaskDto(taskManager.getSubTask(id));
    }

    void deleteTask(Integer id) {
        taskManager.deleteTask(id);
    }

    void deleteSubTask(Integer id) {
        taskManager.deleteSubTask(id);
    }

    void deleteEpicTask(Integer id) {
        taskManager.deleteEpicTask(id);
    }

    void clearTasks() {
        taskManager.clearTasks();
    }

    void clearSubTasks() {
        taskManager.clearSubTasks();
    }

    void clearEpicTasks() {
        taskManager.clearEpicTasks();
    }

    void addTask(TaskDtoForCreate task) {
        taskManager.addTask(mapper.toTask(task));
    }

    void addEpicTask(TaskDtoForCreate epicTask) {
        taskManager.addTask(mapper.toEpicTask(epicTask));
    }

    void addSubTask(TaskDtoForCreate subTaskDto) {
        EpicTask epicTask = (EpicTask) taskManager.getEpicTask(subTaskDto.getEpicTaskId());
        if (epicTask == null) {
            throw new IllegalArgumentException("todo");
        }
        SubTask subTask = mapper.toSubTask(subTaskDto);
        taskManager.addTask(subTask);
        subTask.setEpicTask(epicTask);
    }

    TaskListDto getSubTasks(Integer epicTaskId) {
        Task epicTask = taskManager.getEpicTask(epicTaskId);
        return taskListToTaskListDto(taskManager.getSubTasks((EpicTask) epicTask));
    }

    TaskDto setTaskStatus(Integer taskId, String newStatus) {
        Task foundTask = taskManager.getTask(taskId);
        taskManager.setTaskStatus(foundTask, Status.valueOf(newStatus));
        return mapper.toTaskDto(foundTask);
    }

    void addTasks(TaskListDto tasks) {

    }

    TaskListDto getAllTasks() {
        return taskListToTaskListDto(taskManager.getAllTasks());
    }

    TaskListDto getHistory() {
        return taskListToTaskListDto(taskManager.getHistory());
    }

    TaskListDto getPrioritizedTasks() {
        return taskListToTaskListDto(taskManager.getPrioritizedTasks());
    }

    @NotNull
    private TaskListDto taskListToTaskListDto(List<? extends Task> allTasks) {
        return new TaskListDto(
                allTasks.stream()
                        .map(t -> {
                            if (t instanceof EpicTask) {
                                return mapper.toTaskDto((EpicTask) t);
                            } else if (t instanceof SubTask) {
                                return mapper.toTaskDto((SubTask) t);
                            } else {
                                return mapper.toTaskDto(t);
                            }
                        })
                        .collect(Collectors.toList()));
    }
}
