package kanban.service.dto;

import kanban.manager.tasks.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDtoForCreate {
    private String name;
    private String description;
    private Status status;
    private Long duration;

    private String startTime;

    private Integer epicTaskId;

}
