package kanban.manager.exception;

public class ManagerRestoreException extends ManagerException{

    public static final String DEFAULT_MESSAGE = "Ошибка чтения файла";

    public ManagerRestoreException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public ManagerRestoreException() {
        super(DEFAULT_MESSAGE, null);
    }
}
