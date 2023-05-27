package kanban.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.ToString;

@ToString(callSuper = true, exclude = {"epicTask"})
@JsonTypeName("SubTask")
public class SubTask extends Task {
    @JsonIgnore
    // Цикличность ссылок разорвана для сериализации:
    // * со стороны SubTask не ссылка на эпик, а EpicTaskId
    protected EpicTask epicTask;

    private Integer epicTaskId;

    public SubTask(String name, String description, Status status, EpicTask epicTask) {
        super(name, description, status);
        setEpicTask(epicTask);
    }

    @JsonProperty("epicTaskId")
    public Integer getEpicTaskId() {
        // В момент создания задачи у нее нет id, есть эпик, но его Id будет назначен только при добавлении в менеджер
        // а Id нужен для сериализации
        return (epicTask != null) ? epicTask.getId() : epicTaskId;
    }

    @JsonCreator
    public SubTask(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("status") Status status,
            @JsonProperty("epicTaskId") Integer epicTaskId) {
        super(id, name, description, status);
        this.epicTaskId = epicTaskId;
    }


    @Override
    public void onDelete() {
        epicTask.getSubTasks().remove(this);
        epicTask.updateStatus();
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        epicTask.updateStatus();
    }

    public void setEpicTask(EpicTask epicTask) {
        if (this.epicTask != null) {
            epicTask.getSubTasks().remove(this);
        }
        this.epicTask = epicTask;
        epicTask.addSubTask(this);
    }
}
