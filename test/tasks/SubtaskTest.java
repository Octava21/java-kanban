package tasks;

import managers.Managers;
import managers.TaskManager;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void subtaskCannotBeEpic() {
        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", 1, "2024-04-08T09:00:00", "duration1");
        subTask1.setId(1);
        subTask1.setEpicId(1);

        assertNotEquals(1, subTask1.getEpicId());
    }
}
