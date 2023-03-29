package kanban.tasks;

import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
public class EpicTask extends Task {

    private final Set<SubTask> subTasks = new HashSet<>();

    public EpicTask(String name, String description, Status status) {
        super(name, description, status);
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
