package kanban.manager.config;

import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.manager.tasks.EpicTask;
import kanban.manager.tasks.Status;
import kanban.manager.tasks.SubTask;
import kanban.manager.tasks.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class Config {    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime START_1 = LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER);
    private static final LocalDateTime START_2 = LocalDateTime.parse("2000-01-02 10:00:00", FORMATTER);
    private static final LocalDateTime START_EPIC = LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER);
    private static final LocalDateTime END_1 = LocalDateTime.parse("2000-01-01 02:00:00", FORMATTER);
    private static final LocalDateTime END_2 = LocalDateTime.parse("2000-01-02 13:00:00", FORMATTER);
    private static final LocalDateTime END_EPIC = LocalDateTime.parse("2000-01-02 13:00:00", FORMATTER);
    private static final Long DURATION_1 = 120L;
    private static final Long DURATION_2 = 180L;
    private static final Long DURATION_EPIC = 300L;
    private static final LocalDateTime START_ERR_1 = LocalDateTime.parse("2000-01-01 01:00:00", FORMATTER);
    private static final LocalDateTime START_ERR_2 = LocalDateTime.parse("1999-12-31 23:45:00", FORMATTER);
    private static final LocalDateTime START_ERR_3 = LocalDateTime.parse("2000-01-01 01:45:00", FORMATTER);
    private static final LocalDateTime START_ERR_4 = LocalDateTime.parse("2000-01-01 03:00:00", FORMATTER);
    private static final Long DURATION_ERR_1 = 30L;
    private static final Long DURATION_ERR_2 = 30L;
    private static final Long DURATION_ERR_3 = 30L;
    private static final Long DURATION_ERR_4 = 30L;
    //@Bean
    public TaskManager<Task, Integer> getTaskManager() {
        TaskManager<Task, Integer> manager = Managers.getServerBackedTasksManager();

        EpicTask epicTask1 = new EpicTask("epic1", " ", Status.NEW);
        EpicTask epicTask2 = new EpicTask("epic2", " ", Status.NEW);
        SubTask subTask11 = new SubTask("subTask1.1", " ", Status.NEW, epicTask1);
        SubTask subTask12 = new SubTask("subTask1.2", " ", Status.NEW, epicTask1);
        SubTask subTask21 = new SubTask("subTask2.1", "", Status.NEW, epicTask2);
        Task task3 = new Task("task3", "", Status.NEW);
        Task task4 = new Task("task4", "", Status.NEW);
        manager.addTasks(epicTask1, subTask11, subTask12, epicTask2, subTask21, task3, task4);


        epicTask1 = new EpicTask("epic3", " ", Status.NEW);
        epicTask2 = new EpicTask("epic4", " ", Status.NEW);
        subTask21 = new SubTask("subTask4.1", "", Status.NEW, DURATION_1, START_1, epicTask2);
        SubTask subTask22 = new SubTask("subTask4.2", "", Status.NEW, DURATION_2, START_2, epicTask2);
        manager.addTasks(
                epicTask1,
                epicTask2,
                subTask21,
                subTask22
        );
        return manager;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
