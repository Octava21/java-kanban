package managers;
import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;


class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }
    @Test

    public void testAddAndFindTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        assertEquals(task1.getId(), task2.getId());
    }


    @Test
    public void testAddAndFindEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1", "NEW");
        Epic epic2 = new Epic("Epic 2", "Description 2", "NEW");

        assertEquals(epic1.getId(), epic2.getId());
    }

    @Test
    public void testAddAndFindSubTasks() {
        int epicId1 = 1;
        int epicId2 = 2;

        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", epicId1, "2024-04-08T09:00:00", "duration1");
        Subtask subTask2 = new Subtask("SubTask 2", "Description 2", epicId2, "2024-04-08T10:30:00", "duration2");

        assertEquals(subTask1.getId(), subTask2.getId());
    }

    @Test
    public void testDeleteTaskForId() {
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.addNewTask(task);

        taskManager.deleteTaskById(taskId);

        assertNull(taskManager.getTaskById(taskId));
    }



    @Test
    public void testDeleteEpicForId() {
        Epic epic = new Epic("Epic 1", "Description 1","NEW");
        int epicId = taskManager.addNewEpic(epic);

        taskManager.deleteEpicById(epicId);

        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testDeleteSubTaskForId() {
        int epicId = 1;
        Subtask subtask = new Subtask("SubTask 1", "Description 1", epicId, "2024-04-08T09:00:00" ,  "duration1");
        int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.deleteSubtaskById(subtaskId);

        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1");
        int taskID = taskManager.addNewTask(task);

        task.setName("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.NEW);

        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(taskID);

        assertEquals("Updated Task", updatedTask.getName());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testUpdateEpicStatus() {
        Epic epic = new Epic("Epic 1", "Description 1", "NEW");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", epicId, "2024-04-08T10:00:00", "PT2H");
        Subtask subTask2 = new Subtask("SubTask 2", "Description 2", epicId, "2024-04-08T10:00:00", "PT2H");

        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);

        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        Epic updatedEpic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus());
    }



    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> allTasks = taskManager.getTasks();

        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }


    @Test
    public void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1", "NEW");
        Epic epic2 = new Epic("Epic 2", "Description 2", "NEW");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        List<Epic> allEpics = taskManager.getEpics();

        assertEquals(2, allEpics.size());
        assertTrue(allEpics.contains(epic1));
        assertTrue(allEpics.contains(epic2));
    }

    @Test
    public void testGetAllSubTasks() {
        int epicId1 = 1;
        int epicId2 = 2;
        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", epicId1, "2024-04-08T09:00:00", "duration1");
        Subtask subTask2 = new Subtask("SubTask 2", "Description 2", epicId2,  "2024-04-08T10:30:00", "duration2");


        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);

        List<Subtask> allSubTasks = taskManager.getSubtasks();

        assertEquals(2, allSubTasks.size());
        assertTrue(allSubTasks.contains(subTask1));
        assertTrue(allSubTasks.contains(subTask2));
    }

    @Test
    public void testDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.deleteAllTasks();

        List<Task> allTasks = taskManager.getTasks();

        assertEquals(0, allTasks.size());
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1","NEW");
        Epic epic2 = new Epic("Epic 2", "Description 2","NEW");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        taskManager.deleteAllEpics();

        List<Epic> allEpics = taskManager.getEpics();

        assertEquals(0, allEpics.size());
    }

    @Test
    public void testDeleteAllSubTasks() {
        int epicId1 = 1;
        int epicId2 = 2;

        Subtask subTask1 = new Subtask("SubTask 1", "Description 1", epicId1, "2024-04-08T09:00:00", "duration1");
        Subtask subTask2 = new Subtask("SubTask 2", "Description 2", epicId2, "2024-04-08T10:30:00", "duration2");

        taskManager.addNewSubtask(subTask1);
        taskManager.addNewSubtask(subTask2);

        taskManager.deleteAllSubtasks();

        List<Subtask> allSubTasks = taskManager.getSubtasks();

        assertEquals(0, allSubTasks.size());
    }

}