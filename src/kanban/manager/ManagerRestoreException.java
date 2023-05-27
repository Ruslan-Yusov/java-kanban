package kanban.manager;

public class ManagerRestoreException extends RuntimeException{
    public ManagerRestoreException(Throwable cause) {
        super("Ошибка чтения файла", cause);
    }
}
