package kanban.sprint7;

import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.tasks.EpicTask;
import kanban.tasks.Status;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

class TestSprint7 {
    /**
     * Для расчёта статуса Epic. Граничные условия:
     *    a.   Пустой список подзадач.
     *    b.   Все подзадачи со статусом NEW.
     *    c.    Все подзадачи со статусом DONE.
     *    d.    Подзадачи со статусами NEW и DONE.
     *    e.    Подзадачи со статусом IN_PROGRESS.
     * Для двух менеджеров задач InMemoryTasksManager и FileBackedTasksManager.
     * Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод из интерфейса abstract class TaskManagerTest<T extends TaskManager>.
     * Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
     * Для каждого метода нужно проверить его работу:
     *   a. Со стандартным поведением.
     *   b. С пустым списком задач.
     *   c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
     * Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
     *  a. Пустая история задач.
     *  b. Дублирование.
     *  с. Удаление из истории: начало, середина, конец.
     * Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
     *  a. Пустой список задач.
     *  b. Эпик без подзадач.
     *  c. Пустой список истории.
     *
     *  Добавьте новые поля в задачи:
     * duration — продолжительность задачи, оценка того, сколько времени она займёт в минутах (число);
     * startTime — дата, когда предполагается приступить к выполнению задачи.
     * getEndTime() — время завершения задачи, которое рассчитывается исходя из startTime и duration.
     *
     *  Добавьте в тесты проверку новых полей.
     */
    private TaskManager<Task, Integer> serviceManager;
    private EpicTask epicTask1;
    private EpicTask epicTask2;

    private SubTask subTask21;
    private SubTask subTask22;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime START_1 = LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER);
    private static final LocalDateTime START_2 = LocalDateTime.parse("2000-01-02 10:00:00", FORMATTER);
    private static final LocalDateTime START_EPIC = LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER);
    private static final LocalDateTime END_1 = LocalDateTime.parse("2000-01-01 02:00:00", FORMATTER);
    private static final LocalDateTime END_2 = LocalDateTime.parse("2000-01-02 13:00:00", FORMATTER);
    private static final LocalDateTime END_EPIC = LocalDateTime.parse("2000-01-02 13:00:00", FORMATTER);
    private static final Long DURATION_1 = 120L;
    private static final Long DURATION_2 = 180L;
    private static final Long DURATION_EPIC = 300L;

    @BeforeEach
    public void beforeEach() {
        serviceManager = Managers.getDefault(Managers.getDefaultHistory());
        epicTask1 = new EpicTask("epic1", " ", Status.NEW);
        epicTask2 = new EpicTask("epic2", " ", Status.NEW);
        subTask21 = new SubTask("subTask2.1", "", Status.NEW, DURATION_1, START_1, epicTask2);
        subTask22 = new SubTask("subTask2.2", "", Status.NEW, DURATION_2, START_2, epicTask2);
        serviceManager.addTasks(
                epicTask1,
                epicTask2,
                subTask21,
                subTask22
        );
    }

    @AfterEach
    public void afterEach() {
        serviceManager.clearAllTasks();
    }

    @Test
    @DisplayName("Статус эпика. Без подзадач.")
    void test1a() {
        Assertions.assertEquals(Status.NEW, epicTask1.getStatus());
        epicTask1.setStatus(Status.DONE);
        Assertions.assertEquals(Status.NEW, epicTask1.getStatus());
    }

    @Test
    @DisplayName("Статус эпика. Все подзадачи со статусом NEW.")
    void test1b() {
        Assertions.assertEquals(Status.NEW, epicTask2.getStatus());
    }
    @Test
    @DisplayName("Статус эпика. Все подзадачи со статусом DONE.")
    void test1c() {
        subTask21.setStatus(Status.DONE);
        subTask22.setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, epicTask2.getStatus());
    }

    @Test
    @DisplayName("Статус эпика. Подзадачи со статусами NEW и DONE.")
    void test1d() {
        subTask22.setStatus(Status.DONE);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask2.getStatus());
    }

    @Test
    @DisplayName("Статус эпика. Подзадачи со статусом IN_PROGRESS.")
    void test1e() {
        subTask22.setStatus(Status.IN_PROGRESS);
        subTask22.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask2.getStatus());
    }

    @Test
    @DisplayName("Начало, длительность, завершение задач.")
    void test2() {
        Assertions.assertEquals(START_1,    subTask21.getStartTime());
        Assertions.assertEquals(START_2,    subTask22.getStartTime());
        Assertions.assertEquals(START_EPIC, epicTask2.getStartTime());
        Assertions.assertEquals(END_1,    subTask21.getEndTime());
        Assertions.assertEquals(END_2,    subTask22.getEndTime());
        Assertions.assertEquals(END_EPIC, epicTask2.getEndTime());
        Assertions.assertEquals(DURATION_1,    subTask21.getDuration());
        Assertions.assertEquals(DURATION_2,    subTask22.getDuration());
        Assertions.assertEquals(DURATION_EPIC, epicTask2.getDuration());

        Assertions.assertNull(epicTask1.getStartTime());
        Assertions.assertNull(epicTask1.getEndTime());
        Assertions.assertEquals(0, epicTask1.getDuration());

        System.out.println(serviceManager
                .getPrioritizedTasks()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining(
                        "\n",
                        "\n\nСортировка по началу:\n---------------\n",
                        "\n\n"))
        );
    }
}
