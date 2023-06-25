package kanban.service;

import kanban.manager.tasks.EpicTask;
import kanban.manager.tasks.SubTask;
import kanban.manager.tasks.Task;
import kanban.service.dto.TaskDto;
import kanban.service.dto.TaskDtoForCreate;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    TaskDto toTaskDto(Task task);
    TaskDto toTaskDto(EpicTask task);
    TaskDto toTaskDto(SubTask task);

    Task toTask(TaskDtoForCreate taskDto);
    SubTask toSubTask(TaskDtoForCreate taskDto);
    EpicTask toEpicTask(TaskDtoForCreate taskDto);

    default LocalDateTime toLocalDateTime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }
}
