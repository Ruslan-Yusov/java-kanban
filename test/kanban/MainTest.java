package kanban;

import kanban.manager.ServiceManager;
import kanban.tasks.EpicTask;
import kanban.tasks.Status;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    public void main() {
        ServiceManager serviceManager = new ServiceManager();
        EpicTask epicTask1 = new EpicTask(serviceManager.getNextId(), "epic1", " ", Status.NEW);
        EpicTask epicTask2 = new EpicTask(serviceManager.getNextId(), "epic2", " ", Status.NEW);
        SubTask subTask11 = new SubTask(serviceManager.getNextId(), "subTask1.1", " ", Status.NEW, epicTask1 );
        SubTask subTask12 = new SubTask(serviceManager.getNextId(), "subTask1.2", " ", Status.NEW, epicTask1 );
        SubTask subTask21 = new SubTask(serviceManager.getNextId(), "subTask2.1", "", Status.NEW, epicTask2);
        Task task3 = new Task(serviceManager.getNextId(), "task3", "", Status.NEW);
        Task task4 = new Task(serviceManager.getNextId(), "task4", "", Status.NEW);
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
        Assertions.assertNull(serviceManager.getTask(1));
        Assertions.assertEquals(4, serviceManager.getTasks().size());
        serviceManager.deleteTask(6);
        Assertions.assertNull(serviceManager.getTask(6));
        Assertions.assertEquals(3, serviceManager.getTasks().size());
        System.out.println(serviceManager);

        Assertions.assertEquals(3, serviceManager.getAllTasks().size());

    }


}