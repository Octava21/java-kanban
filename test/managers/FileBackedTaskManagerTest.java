package managers;


import managers.TaskManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private TaskManager manager;
    private File testFile;

    @BeforeEach
    public void setUp() {
        try {
            testFile = File.createTempFile("testFile", ".txt");
            manager = new FileBackedTasksManager(testFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveAndLoadEmptyFile() {
        assertEquals(0, manager.getTasks().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void testSaveAndLoadSomeTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        assertEquals(2, manager.getTasks().size());
        assertEquals(0, manager.getHistory().size());

        // Reinitialize manager to simulate loading from file
        manager = new FileBackedTasksManager(testFile);

        assertEquals(2, manager.getTasks().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void testIOExceptionIsThrown() {
        assertThrows(IOException.class, () -> {
            // Attempt to create a manager with a directory instead of a file
            manager = new FileBackedTasksManager(new File("/path/to/directory"));
        });
    }
}
