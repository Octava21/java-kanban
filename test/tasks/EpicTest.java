package tasks;

import model.Task;
import model.Epic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {

    @Test
    public void testEpicCannotBeSubTaskOfItself() {
        Epic epic = new Epic("Epic 1", "Description 1", "NEW");
        epic.setId(1);

        epic.addSubTaskId(1);

        assertEquals(0, epic.getSubTaskId().size());
    }

}