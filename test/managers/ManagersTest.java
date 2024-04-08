package managers;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }
    @Test
    public void getDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Экземпляр InMemoryTaskManager не создан");
    }

    @Test
    public void getDefaultHistoryNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Экземпляр InMemoryHistoryManager не создан");
    }

}