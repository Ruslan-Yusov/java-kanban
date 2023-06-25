package kanban.sprint7;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.manager.tasks.EpicTask;
import kanban.manager.tasks.Status;
import kanban.manager.tasks.SubTask;
import kanban.manager.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

@Disabled
@DisplayName("Sprint 7. Для запуска вручную.")
public class TaskManagerInMemoryTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void initManager() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
        Task baseTask = new Task("baseTask", " ", Status.NEW);
        EpicTask epicTask1 = new EpicTask("epic1", " ", Status.NEW);
        Task subTask11 = new SubTask("subTask_1.1", "", Status.NEW, epicTask1);
        Task subTask12 = new SubTask("subTask_1.2", "", Status.NEW, epicTask1);
        EpicTask epicTask2 = new EpicTask("epic2", " ", Status.NEW);
        Task subTask21 = new SubTask("subTask_2.1", "", Status.NEW, epicTask2);
        Task subTask22 = new SubTask("subTask_2.2", "", Status.NEW, epicTask2);
        taskManager.addTasks(
                baseTask,
                epicTask1,
                subTask11,
                subTask12,
                epicTask2,
                subTask21,
                subTask22
        );
    }
}
