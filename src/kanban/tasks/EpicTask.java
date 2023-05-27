package kanban.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@JsonTypeName("EpicTask")
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
}
