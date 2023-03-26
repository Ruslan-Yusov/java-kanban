package kanban.tasks;

import lombok.*;

@Data
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {
    protected int id;
    protected String name;
    protected String description;
    @Setter(AccessLevel.PUBLIC)
    protected Status status;

    public void onDelete() {
    }
}
