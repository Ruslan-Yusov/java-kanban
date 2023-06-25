package kanban.sprint6;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import kanban.manager.FileBackedTasksManager;
import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.manager.tasks.EpicTask;
import kanban.manager.tasks.Status;
import kanban.manager.tasks.SubTask;
import kanban.manager.tasks.Task;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Disabled
@DisplayName("Sprint 6. Для запуска вручную.")
class TestSprint6 {
    @Test
    void oneManagerTest() {
        TaskManager<Task, Integer> serviceManager = Managers.getDefault(Managers.getDefaultHistory());
        EpicTask epicTask1 = new EpicTask("epic1", " ", Status.NEW);
        EpicTask epicTask2 = new EpicTask("epic2", " ", Status.NEW);
        SubTask subTask11 = new SubTask("subTask1.1", " ", Status.NEW, epicTask1);
        SubTask subTask12 = new SubTask("subTask1.2", " ", Status.NEW, epicTask1);
        SubTask subTask21 = new SubTask("subTask2.1", "", Status.NEW, epicTask2);
        Task task3 = new Task("task3", "", Status.NEW);
        Task task4 = new Task("task4", "", Status.NEW);
        serviceManager.addTasks(epicTask1, subTask11, subTask12, epicTask2, subTask21, task3, task4);
        System.out.println(serviceManager);

        subTask21.setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, subTask21.getStatus());
        Assertions.assertEquals(Status.DONE, epicTask2.getStatus());
        System.out.println(serviceManager);

        subTask12.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask1.getStatus());
        Assertions.assertEquals(Status.IN_PROGRESS, subTask12.getStatus());
        System.out.println(serviceManager);

        subTask12.setStatus(Status.DONE);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask1.getStatus());
        Assertions.assertEquals(Status.DONE, subTask12.getStatus());
        subTask11.setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, epicTask1.getStatus());
        Assertions.assertEquals(Status.DONE, subTask12.getStatus());
        System.out.println(serviceManager);

        serviceManager.deleteTask(1);
        Assertions.assertNotNull(serviceManager.getTask(1));
        serviceManager.deleteEpicTask(1);
        Assertions.assertNull(serviceManager.getTask(1));
        Assertions.assertEquals(4, serviceManager.getAllTasks().size());
        serviceManager.deleteTask(6);
        Assertions.assertNull(serviceManager.getTask(6));
        Assertions.assertEquals(3, serviceManager.getAllTasks().size());
        System.out.println(serviceManager);

        Assertions.assertEquals(3, serviceManager.getAllTasks().size());

        // 7 - простая задача, 4 - эпик
        System.out.println(serviceManager.getEpicTask(7));
        Assertions.assertNull(serviceManager.getEpicTask(7));
        System.out.println(serviceManager.getEpicTask(4));
        Assertions.assertEquals(epicTask2, serviceManager.getEpicTask(4));

        // 7 - простая задача, 5 - подзадача
        System.out.println(serviceManager.getSubTask(7));
        Assertions.assertNull(serviceManager.getSubTask(7));
        System.out.println(serviceManager.getSubTask(5));
        Assertions.assertEquals(subTask21, serviceManager.getSubTask(5));
        // 7 никогда не была запрошена как обычная задача

        System.out.println(serviceManager
                .getHistory()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n", "\n\nИстория действий:\n---------------:\n", "\n\n"))
        );
        serviceManager.getTask(7);
        serviceManager.getEpicTask(4);
        // Когда запрашиваем удаленную задачу ее порядок в истории не меняется, то есть меняется порядок 7, 4 и 5 задачи
        serviceManager.getEpicTask(1);
        serviceManager.getSubTask(5);
        System.out.println(serviceManager
                .getHistory()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n", "\n\nИстория действий:\n---------------:\n", "\n\n"))
        );
        serviceManager.clearAllTasks();
        System.out.println("Все задачи удалены.");
        Assertions.assertEquals(0, serviceManager.getAllTasks().size());
        System.out.println(serviceManager);

    }

    private static class TaskList extends ArrayList<Task> {
        public static TaskList of(Task task) {
            TaskList taskList = new TaskList();
            taskList.add(task);
            return taskList;
        }
    }

    @Test
    void testMapper() {
        ObjectMapper objectMapper = getObjectMapper();
        Task task1 = new Task(123, "name", "dsc", Status.NEW);
        String json1;
        try {
            json1 = objectMapper.writeValueAsString(task1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            TaskList list = TaskList.of(task1);
            json1 = objectMapper.writeValueAsString(list);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String json2 = "{\"TaskList\":[{\"Task\":{\"id\":745,\"name\":\"name745\",\"description\":\"dsc\",\"status\":\"NEW\"}}]}";
        TaskList taskList2 = null;
        try {
            taskList2 = objectMapper.readValue(json2, TaskList.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Assertions.assertNotNull(taskList2);
        Assertions.assertNotNull(taskList2.get(0));
        Assertions.assertEquals(745, taskList2.get(0).getId());
        Assertions.assertEquals("name745", taskList2.get(0).getName());
    }

    @NotNull
    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Test
    void twoManagersTest() {
        TaskManager<Task, Integer> taskManager1 = Managers.getFileTaskManager(Managers.getDefaultHistory());

        Task baseTask = new Task("baseTask", " ", Status.NEW);
        EpicTask epicTask1 = new EpicTask("epic1", " ", Status.NEW);
        Task subTask11 = new SubTask("subTask_1.1", "", Status.NEW, epicTask1);
        Task subTask12 = new SubTask("subTask_1.2", "", Status.NEW, epicTask1);
        EpicTask epicTask2 = new EpicTask("epic2", " ", Status.NEW);
        Task subTask21 = new SubTask("subTask_2.1", "", Status.NEW, epicTask2);
        Task subTask22 = new SubTask("subTask_2.2", "", Status.NEW, epicTask2);
        taskManager1.addTasks(
                baseTask,
                epicTask1,
                subTask11,
                subTask12,
                epicTask2,
                subTask21,
                subTask22
        );
        System.out.println(taskManager1);

        taskManager1.getTask(1);
        taskManager1.getEpicTask(2);
        taskManager1.getSubTask(4);
        System.out.println(taskManager1
                .getHistory()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n", "\n\nИстория действий (1):\n---------------:\n", "\n\n"))
        );

        TaskManager<Task, Integer> taskManager2 = FileBackedTasksManager.restoreFromFile(Managers.DEFAULT_TASK_MANAGER_FILE);
        System.out.println(taskManager2);
        System.out.println(taskManager1
                .getHistory()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n", "\n\nИстория действий (2):\n---------------\n", "\n\n"))
        );


        Assertions.assertNotNull(taskManager2);
        Assertions.assertNotNull(taskManager2.getAllTasks());
        ObjectMapper om = getObjectMapper();
        try {
            Assertions.assertEquals(
                    om.writeValueAsString(taskManager1.getAllTasks()),
                    om.writeValueAsString(taskManager2.getAllTasks()));
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException !!!");
        }
    }
}
