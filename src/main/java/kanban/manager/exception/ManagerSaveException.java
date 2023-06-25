package kanban.manager.exception;

public class ManagerSaveException extends ManagerException {

    public static final String DEFAULT_MESSAGE = "Ошибка сохранения файла";

    public ManagerSaveException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public ManagerSaveException() {
        super(DEFAULT_MESSAGE, null);
    }
}
