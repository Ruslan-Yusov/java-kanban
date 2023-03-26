package kanban.tasks;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true, exclude = {"epicTask"})
public class SubTask extends Task {
    EpicTask epicTask;

    public SubTask(int id, String name, String description, Status status, EpicTask epicTask) {
        super(id, name, description, status);
        this.epicTask = epicTask;
        epicTask.addSubTask(this);
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public void onDelete() {
        epicTask.getSubTasks().remove(this);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        epicTask.updateStatus();
    }
}
