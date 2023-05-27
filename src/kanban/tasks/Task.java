package kanban.tasks;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubTask.class, name = "SubTask"),
        @JsonSubTypes.Type(value = EpicTask.class, name = "EpicTask")
})
public class Task {

    private Integer id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @JsonCreator
    public Task(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("status") Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void onDelete() {
        // null
    }

    @Override
    public int hashCode() {
        // В момент создания задачи у нее нет id, есть эпик, нельзя задачу без хеша добавить в хеш сет подзадач в эпике
        return id != null ? id : name.hashCode();
    }
}
