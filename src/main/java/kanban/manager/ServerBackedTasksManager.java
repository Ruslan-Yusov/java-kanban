package kanban.manager;

import kanban.service.KVClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ServerBackedTasksManager extends FileBackedTasksManager {

    @Autowired
    private KVClient kvClient;

    public ServerBackedTasksManager() {
        super(Managers.getDefaultHistory(), null);
    }

    @Override
    protected void validateFilePath() {
        //
    }

    @Override
    protected void saveAsString(String data) {
        kvClient.save(data);
    }
}
