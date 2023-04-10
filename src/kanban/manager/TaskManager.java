package kanban.manager;

import kanban.tasks.*;

import java.util.List;

/**
 * @param <T> базовый тип задач
 * @param <N> тип идентификатора задач
 */
public interface TaskManager<T, N extends Number> {

    /**
     * Поиск по id в {@link Task}
     *
     * @param id индентификатор
     * @return объект {@link Task}
     */
    T getTask(N id);

    /**
     * Поиск по id в {@link EpicTask}
     *
     * @param id индентификатор
     * @return объект {@link EpicTask}
     */
    T getEpicTask(N id);

    /**
     * Поиск по id в {@link SubTask}
     *
     * @param id индентификатор
     * @return объект {@link SubTask}
     */
    T getSubTask(N id);

    /**
     * Удаление базовой задачи
     *
     * @param id индентификатор
     */
    void deleteTask(N id);

    /**
     * Удаление эпика
     *
     * @param id индентификатор
     */
    void deleteSubTask(N id);

    /**
     * Удаление подзадачи
     *
     * @param id индентификатор
     */
    void deleteEpicTask(N id);

    /**
     * Удаление всех задач ({@link Task}, {@link EpicTask}, {@link SubTask})
     */
    void clearAllTasks();

    /**
     * Удаление всех задач ({@link Task})
     */
    void clearTasks();

    /**
     * Удаление всех подзадач ({@link SubTask})
     */
    void clearSubTasks();

    /**
     * Удаление всех эпиков ({@link EpicTask})
     */
    void clearEpicTasks();

    /**
     * Добавление задачи
     *
     * @param task объект типа {@link Task}
     */
    void addTask(T task);

    /**
     * Добавление эпика
     *
     * @param epicTask объект типа {@link EpicTask}
     */
    <E extends T> void addEpicTask(E epicTask);

    /**
     * Добавление подзадачи
     *
     * @param subTask объект типа {@link SubTask}
     */
    <S extends T> void addSubTask(S subTask);

    /**
     * Обновление задачи
     *
     * @param task объект {@link Task}
     */
    void updateTask(T task);

    /**
     * Обновление Эпика
     *
     * @param epicTask объект {@link EpicTask}
     */
    <E extends T> void updateEpicTask(E epicTask);

    /**
     * Обновление подзадачи
     *
     * @param subTask объект {@link SubTask}
     */
    <S extends T> void updateSubTask(S subTask);

    /**
     * Получение всех подзадач в рамках одного эпика
     *
     * @param epicTask объект типа {@link EpicTask}
     * @return список всех подзадач
     */
    <E extends ParentTask<? extends Task>> List<? extends T> getSubTasks(E epicTask);

    /**
     * Установление статусов
     *
     * @param task      объект типа {@link Task}
     * @param newStatus новый статус из класса {@link Status}
     */
    <A extends T> A setTaskStatus(A task, Status newStatus);

    /**
     * Добавление задач
     *
     * @param tasks объект типа {@link Task}
     */
    void addTasks(T... tasks);

    /**
     * Получение всех задач
     *
     * @return список всех задач
     */
    List<? extends T> getAllTasks();

    /**
     * Получение просмотра всех задач
     *
     * @return последние просмотренные задачи
     */
    List<T> getHistory();
}