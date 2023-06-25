package kanban.manager.exception;

public class UnknownTaskException extends TaskException {

    public static final String DEFAULT_MESSAGE = "Неизвестная задача";

    public UnknownTaskException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public UnknownTaskException() {
        super(DEFAULT_MESSAGE, null);
    }
}
