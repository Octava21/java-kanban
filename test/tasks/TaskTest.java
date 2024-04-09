package tasks;

import managers.Managers;
import managers.TaskManager;
import model.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;

public class TaskTest {
    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void taskEquality() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 1", "Description 1");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void calculateEndTime() {
        Task task1 = new Task("Task 1", "Description 1");

        task1.setId(1);

        // Предположим, что для задачи установлено время начала: 2024-03-04T19:00:00
        LocalDateTime expected = LocalDateTime.of(2024, Month.MARCH, 4, 21, 0); // 2 часа после начала
        LocalDateTime actual = manager.getTaskById(1).getEndTime();

        assertEquals(expected, actual);
    }
}
