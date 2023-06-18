package kanban.manager.exception;

public class IntersectedTaskException extends TaskException {

    public static final String DEFAULT_MESSAGE = "Задача пересекается по времени с существующей";

    public IntersectedTaskException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public IntersectedTaskException() {
        super(DEFAULT_MESSAGE, null);
    }
}
