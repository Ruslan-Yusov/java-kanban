package kanban.tasks;

import lombok.ToString;

@ToString(callSuper = true, exclude = {"epicTask"})
public class SubTask extends Task {
    private final EpicTask epicTask;

    public SubTask(String name, String description, Status status, EpicTask epicTask) {
        super(name, description, status);
        this.epicTask = epicTask;
        epicTask.addSubTask(this);
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
}
