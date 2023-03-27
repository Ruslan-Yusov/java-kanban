package kanban.tasks;

import lombok.*;

@Data
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {
    private int id;
    private String name;
    private String description;
    @Setter(AccessLevel.PUBLIC)
    private Status status;

    public void onDelete() {
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
