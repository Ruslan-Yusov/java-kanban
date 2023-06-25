package kanban.manager.tasks;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;

import static java.util.Optional.ofNullable;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubTask.class, name = "SubTask"),
        @JsonSubTypes.Type(value = EpicTask.class, name = "EpicTask")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Task {

    @JsonIgnore
    public static final Comparator<Task> COMPARATOR_BY_START =
            Comparator.nullsLast(Comparator.comparing(Task::getStartTime));

    private Integer id;
    private String name;
    private String description;
    private Status status;
    private Long duration; // продолжительность задачи, оценка того, сколько времени она займёт в минутах (число);
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // дата, когда предполагается приступить к выполнению задачи.

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @JsonIgnore
    public LocalDateTime getEndTime() {
        return ofNullable(startTime)
                .flatMap(st -> ofNullable(duration).map(st::plusMinutes))
                .orElse(null);
    }
}
