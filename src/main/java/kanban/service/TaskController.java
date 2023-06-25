package kanban.service;

import kanban.service.dto.TaskDto;
import kanban.service.dto.TaskDtoForCreate;
import kanban.service.dto.TaskListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
public class TaskController {
    @Autowired
    private TaskManagerService taskManagerService;
    @GetMapping("/tasks/task")
    public TaskListDto getTasks() {
        return taskManagerService.getTasks();
    }
    @GetMapping("/tasks/task/")
    TaskDto getTask(@RequestParam("id") Integer id) {
        return taskManagerService.getTask(id);
    }

    @GetMapping("/tasks/epic/")
    TaskDto getEpicTask(@RequestParam("id") Integer id) {
        return taskManagerService.getEpicTask(id);
    }

    @GetMapping("/tasks/subtask/")
    TaskDto getSubTask(@RequestParam("id") Integer id) {
        return taskManagerService.getSubTask(id);
    }

    @DeleteMapping("/tasks/task/")
    void deleteTask(@RequestParam("id") Integer id) {
        taskManagerService.deleteTask(id);
    }
    @DeleteMapping("/tasks/subtask/")
    void deleteSubTask(@RequestParam("id") Integer id) {
        taskManagerService.deleteSubTask(id);
    }

    @DeleteMapping("/tasks/epic/")
    void deleteEpicTask(@RequestParam("id") Integer id) {
        taskManagerService.deleteEpicTask(id);
    }

    @DeleteMapping("/tasks/task")
    void clearTasks() {
        taskManagerService.clearTasks();
    }

    @DeleteMapping("/tasks/subtask")
    void clearSubTasks() {
        taskManagerService.clearSubTasks();
    }

    @DeleteMapping("/tasks/epic")
    void clearEpicTasks() {
        taskManagerService.clearEpicTasks();
    }

    @PostMapping("/tasks/task/")
    void addTask(@RequestBody TaskDtoForCreate task) {
        taskManagerService.addTask(task);
    }

    @PostMapping("/tasks/epic/")
    void addEpicTask(@RequestBody TaskDtoForCreate epicTask) {
        taskManagerService.addEpicTask(epicTask);
    }

    @PostMapping("/tasks/subtask/")
    void addSubTask(@RequestBody TaskDtoForCreate subTask) {
        taskManagerService.addSubTask(subTask);
    }

    @GetMapping("/tasks/subtasks/epic/")
    TaskListDto getSubTasks(@RequestParam("id") Integer epicTaskId) {
        return taskManagerService.getSubTasks(epicTaskId);
    }

    @PutMapping("/tasks/task/{id}/")
    TaskDto setTaskStatus(
            @PathVariable("id") Integer taskId,
            @RequestParam("status") String newStatus
    ) {
        return taskManagerService.setTaskStatus(taskId, newStatus);
    }

    @GetMapping("/tasks/history")
    TaskListDto getHistory() {
        return taskManagerService.getHistory();
    }

    @GetMapping("/tasks/")
    TaskListDto getPrioritizedTasks() {
        return taskManagerService.getPrioritizedTasks();
    }
}
