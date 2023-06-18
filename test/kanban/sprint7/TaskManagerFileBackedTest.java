package kanban.sprint7;

import kanban.manager.FileBackedTasksManager;
import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.tasks.EpicTask;
import kanban.tasks.Status;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskManagerFileBackedTest extends TaskManagerTest<FileBackedTasksManager> {

    public static final String DATA_FILE = "TasksForAutotest.json";
    public static final String DATA_FILE_COPY = "TasksForAutotest-copy.json";

    @BeforeEach
    public void initManager() {

        historyManager = Managers.getDefaultHistory();
        taskManager = FileBackedTasksManager.restoreFromFile(DATA_FILE);
        taskManager.setFilePath(DATA_FILE_COPY);
    }

}
