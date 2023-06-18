package kanban.sprint7;

import kanban.manager.FileBackedTasksManager;
import kanban.manager.Managers;
import org.junit.jupiter.api.BeforeEach;

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
