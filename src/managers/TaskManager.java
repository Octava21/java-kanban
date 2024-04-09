package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    Map<Integer, Task> getTaskHashMap();

    Map<Integer, Epic> getEpicHashMap();

    List<Subtask> getSubTaskList();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    List<Task> getHistory();
    List<Subtask> getSubtasksByEpicId(int epicId);

    Set<Task> getPrioritizedTasks();
}
