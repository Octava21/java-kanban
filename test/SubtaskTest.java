import model.Subtask;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    @Test
    public void SubTaskCannotBeEpic() {
        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", "NEW", 1);
        subTask1.setId(1);

        subTask1.setEpicId(1);

        assertNotEquals(1, subTask1.getEpicId());
    }


}