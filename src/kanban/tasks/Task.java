package kanban.tasks;

import lombok.*;

@Data
public class Task {

    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void onDelete() {
        // null
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
