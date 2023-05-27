package kanban.manager;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(Throwable cause) {
        super("Ошибка сохранения файла", cause);
    }
}
