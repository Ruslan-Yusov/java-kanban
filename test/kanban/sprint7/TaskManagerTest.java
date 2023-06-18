package kanban.sprint7;

import kanban.manager.HistoryManager;
import kanban.manager.TaskManager;
import kanban.tasks.*;
import org.junit.jupiter.api.*;

import java.util.List;

abstract class TaskManagerTest<T extends TaskManager<Task, Integer>> {
    protected T taskManager;

    protected HistoryManager<Task> historyManager;

    private static final int TASK_ID = 1;
    private static final int EPIC_1_ID = 2;
    private static final int EPIC_SUBTASK_11_ID = 3;
    private static final int EPIC_SUBTASK_12_ID = 4;
    private static final int EPIC_2_ID = 5;
    private static final int EPIC_SUBTASK_21_ID = 6;
    private static final int EPIC_SUBTASK_22_ID = 7;

    @AfterEach
    public void afterEach() {
        taskManager.clearAllTasks();
    }
    
    @Test
    @DisplayName("Статусы назначаются верно")
    public void testStandart() {
        Task baseTask = taskManager.getTask(TASK_ID);
        Assertions.assertNotNull(baseTask);
        Assertions.assertEquals(TASK_ID, baseTask.getId());
        
        Task epicTask1 = taskManager.getEpicTask(EPIC_1_ID);
        Assertions.assertNotNull(epicTask1);
        Assertions.assertInstanceOf(EpicTask.class, epicTask1);
        Assertions.assertEquals(EPIC_1_ID, epicTask1.getId());


        Task subTask11 = taskManager.getSubTask(EPIC_SUBTASK_11_ID);
        Assertions.assertNotNull(subTask11);
        Assertions.assertInstanceOf(SubTask.class, subTask11);
        Assertions.assertEquals(EPIC_SUBTASK_11_ID, subTask11.getId());
        
        Task subTask12 = taskManager.getSubTask(EPIC_SUBTASK_12_ID);
        Assertions.assertNotNull(subTask12);
        Assertions.assertEquals(EPIC_SUBTASK_12_ID, subTask12.getId());
        Assertions.assertInstanceOf(SubTask.class, subTask12);
        Assertions.assertNotNull(((SubTask)subTask12).getEpicTaskId());
        Assertions.assertEquals(EPIC_1_ID,((SubTask)subTask12).getEpicTaskId());
    }
    
    @Test
    @DisplayName("Тестирование всех методов интерфейса")
    public void testAll() {

        Task foundTask = taskManager.getTask(TASK_ID);
        Assertions.assertNotNull(foundTask);
        Assertions.assertInstanceOf(Task.class, foundTask);
        Task foundEpicTask = taskManager.getEpicTask(EPIC_1_ID);
        Assertions.assertNotNull(foundEpicTask);
        Assertions.assertInstanceOf(Task.class, foundEpicTask);
        Task foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_11_ID);
        Assertions.assertNotNull(foundSubTask);
        Assertions.assertInstanceOf(Task.class, foundSubTask);

        taskManager.deleteTask(TASK_ID);
        foundTask = taskManager.getTask(TASK_ID);
        Assertions.assertNull(foundTask);
        taskManager.deleteSubTask(EPIC_SUBTASK_11_ID);
        foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_11_ID);
        Assertions.assertNull(foundSubTask);
        taskManager.deleteEpicTask(EPIC_1_ID);
        foundEpicTask = taskManager.getEpicTask(EPIC_1_ID);
        Assertions.assertNull(foundEpicTask);
        foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_12_ID);
        Assertions.assertNull(foundSubTask);

        taskManager.clearTasks();
        taskManager.clearSubTasks();
        foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_21_ID);
        Assertions.assertNull(foundSubTask);
        taskManager.clearEpicTasks();
        foundEpicTask = taskManager.getEpicTask(EPIC_2_ID);
        Assertions.assertNull(foundEpicTask);

        taskManager.clearAllTasks();
        Assertions.assertNotNull(taskManager.getAllTasks());
        Assertions.assertEquals(0, taskManager.getAllTasks().size());
        foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_11_ID);
        Assertions.assertNull(foundSubTask);
        foundSubTask = taskManager.getSubTask(EPIC_SUBTASK_21_ID);
        Assertions.assertNull(foundSubTask);
        foundEpicTask = taskManager.getEpicTask(EPIC_2_ID);
        Assertions.assertNull(foundEpicTask);
        foundTask = taskManager.getTask(TASK_ID);
        Assertions.assertNull(foundTask);

        EpicTask epicTask = new EpicTask("name", "description", Status.NEW);
        SubTask subTask = new SubTask("name", "description", Status.NEW, epicTask);
        Task task = new Task("name", "description", Status.NEW);
        taskManager.addEpicTask(epicTask);
        Assertions.assertNotNull(taskManager.getEpicTask(8));
        Assertions.assertEquals(epicTask.toString(), taskManager.getEpicTask(8).toString());
        taskManager.addSubTask(subTask);
        Assertions.assertNotNull(taskManager.getSubTask(9));
        Assertions.assertEquals(subTask.toString(), taskManager.getSubTask(9).toString());
        taskManager.addTask(task);
        Assertions.assertNotNull(taskManager.getTask(10));
        Assertions.assertEquals(task.toString(), taskManager.getTask(10).toString());
        epicTask.setName("newEpicTask");
        subTask.setName("newSubTask");
        task.setName("newTask");
        taskManager.updateTask(task);
        Assertions.assertEquals(task.toString(), taskManager.getTask(10).toString());
        taskManager.updateEpicTask(epicTask);
        Assertions.assertEquals(epicTask.toString(), taskManager.getEpicTask(8).toString());
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(subTask.toString(), taskManager.getSubTask(9).toString());
        List<? extends Task> subTasks = taskManager.getSubTasks(epicTask);
        Assertions.assertNotNull(subTasks);
        Assertions.assertEquals(1, subTasks.size());
        List<? extends Task> tasks = taskManager.getAllTasks();
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(3, tasks.size());
        List<Task> history = taskManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertNotEquals(0, history.size());
    }

    @Test
    @DisplayName("Менеджер истории работает корректно")
    public void testHistoryManager() {
        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertEquals(0, history.size());
        Task foundTask = taskManager.getTask(TASK_ID);
        historyManager.add(foundTask);

        List<Task> newHistory = historyManager.getHistory();
        Assertions.assertNotNull(newHistory);
        Assertions.assertEquals(1, newHistory.size());
        Assertions.assertNotNull(newHistory.get(0));
        Assertions.assertEquals(foundTask.toString(), newHistory.get(0).toString());

        historyManager.remove(foundTask.getId());
        newHistory = historyManager.getHistory();
        Assertions.assertNotNull(newHistory);
        Assertions.assertEquals(0, newHistory.size());

        historyManager.add(taskManager.getEpicTask(EPIC_1_ID));
        historyManager.add(taskManager.getEpicTask(EPIC_2_ID));
        historyManager.add(taskManager.getSubTask(EPIC_SUBTASK_21_ID));

        history = historyManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertEquals(3, history.size());
        Assertions.assertEquals(EPIC_1_ID, history.get(0).getId());
        Assertions.assertEquals(EPIC_SUBTASK_21_ID, history.get(2).getId());

        // Удаление из начала
        historyManager.remove(EPIC_1_ID);
        history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(EPIC_2_ID, history.get(0).getId());
        Assertions.assertEquals(EPIC_SUBTASK_21_ID, history.get(1).getId());

        // Полная очистка (1)
        historyManager.clear();
        newHistory = historyManager.getHistory();
        Assertions.assertNotNull(newHistory);
        Assertions.assertEquals(0, newHistory.size());

        // Удаление из середины
        historyManager.add(taskManager.getEpicTask(EPIC_1_ID));
        historyManager.add(taskManager.getEpicTask(EPIC_2_ID));
        historyManager.add(taskManager.getSubTask(EPIC_SUBTASK_21_ID));
        historyManager.remove(EPIC_2_ID);
        history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(EPIC_1_ID, history.get(0).getId());
        Assertions.assertEquals(EPIC_SUBTASK_21_ID, history.get(1).getId());

        // Полная очистка (2)
        historyManager.clear();
        newHistory = historyManager.getHistory();
        Assertions.assertNotNull(newHistory);
        Assertions.assertEquals(0, newHistory.size());

        // Удаление из конца
        historyManager.add(taskManager.getEpicTask(EPIC_1_ID));
        historyManager.add(taskManager.getEpicTask(EPIC_2_ID));
        historyManager.add(taskManager.getSubTask(EPIC_SUBTASK_21_ID));
        historyManager.remove(EPIC_SUBTASK_21_ID);
        history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(EPIC_1_ID, history.get(0).getId());
        Assertions.assertEquals(EPIC_2_ID, history.get(1).getId());

        // Дублирование (1) - задача из конца списка
        historyManager.add(taskManager.getEpicTask(EPIC_1_ID));
        history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(EPIC_1_ID, history.get(1).getId());
        Assertions.assertEquals(EPIC_2_ID, history.get(0).getId());

        // Дублирование (2) - задача из начала списка
        historyManager.add(taskManager.getEpicTask(EPIC_1_ID));
        history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(EPIC_1_ID, history.get(1).getId());
        Assertions.assertEquals(EPIC_2_ID, history.get(0).getId());
    }
}
