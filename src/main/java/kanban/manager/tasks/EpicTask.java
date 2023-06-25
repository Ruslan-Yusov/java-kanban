package kanban.manager.tasks;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@JsonTypeName("EpicTask")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class EpicTask extends Task implements ParentTask<SubTask> {

    @JsonIgnore
    // Цикличность ссылок разорвана для сериализации:
    // * со стороны EpicTask нет ссылок на подзадачи
    private final Set<SubTask> subTasks = new HashSet<>();

    public EpicTask(String name, String description, Status status) {
        super(name, description, status);
    }

    @JsonCreator
    public EpicTask(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("status") Status status) {
        super(id, name, description, status);
    }

    @Override
    public void setStatus(Status status) {
        updateStatus();
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            super.setStatus(Status.NEW);
        } else {
            if (subTasks.stream().map(Task::getStatus).allMatch(Status.NEW::equals)) {
                super.setStatus(Status.NEW);
            } else if (subTasks.stream().map(Task::getStatus).allMatch(Status.DONE::equals)) {
                super.setStatus(Status.DONE);
            } else {
                super.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        updateStatus();
    }

    @Override
    public void onDelete() {
        subTasks.clear();
        updateStatus();
    }

    @Override
    @JsonIgnore
    public Long getDuration() {
        return subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
    }

    @Override
    @JsonIgnore
    public LocalDateTime getStartTime() {
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    @JsonIgnore
    public LocalDateTime getEndTime() {
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }
}
