package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int nextId = 0;


    // Добавление новой task
   @Override
    public int addNewTask(Task task) {
        task.setId(++nextId);
        tasks.put(task.getId(), task);
        return task.getId();
    }
    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++nextId);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }
    @Override
    public int addNewSubtask(Subtask subtask) {
        subtask.setId(++nextId);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getId());
           TaskStatus status = calculateEpicStatus(epic.getId());
        }

        return subtask.getId();
    }

    @Override
    // Получение списка task
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение task по ID
    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Удаление всех списка всех задач (Задача/Эпик/Подзадача)
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    // ОБНОВЛЕНИЕ ЗАДАЧ/ПОДЗАДАЧ/ЭПИКОВ
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
        }
    }
    @Override
    public void updateTask(Task task){
        tasks.put(task.getId(),task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);

            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                TaskStatus status = calculateEpicStatus(epic.getId());
                epic.setStatus(status.name());
            }
        }
    }


    // Удаление по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            subtasks.remove(id);
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                getSubtasks().remove(subtask);
               TaskStatus status = calculateEpicStatus(epic.getId());

            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (Subtask subtask : getSubtasks()) {
                deleteSubtaskById(subtask.getId());
            }
            epics.remove(id);
        }
    }

    // Получение списка всех подзадач эпика
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            List<Subtask> subtasks = new ArrayList<>();
            for (Integer subtaskId : epic.getSubTaskId()) {
                Subtask subtask = getSubtaskById(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                }
            }
            return subtasks;
        }
        return new ArrayList<>();
    }



    // Расчет статуса эпика по id
    private TaskStatus calculateEpicStatus(int epicId) {
        ArrayList<Integer> epicSubtaskIds = epics.get(epicId).getSubTaskId();
        int newTask = 0;
        int inProgress = 0;
        int doneTask = 0;

        for (int subtaskId : epicSubtaskIds) {
            TaskStatus subtaskStatus = TaskStatus.valueOf(subtasks.get(subtaskId).getStatus());

            switch (subtaskStatus) {
                case DONE:
                    doneTask++;
                    break;
                case NEW:
                    newTask++;
                    break;
                case IN_PROGRESS:
                    return TaskStatus.IN_PROGRESS; // Если есть IN_PROGRESS, возвращаем сразу
                // Добавьте другие статусы при необходимости
                default:
                    break;
            }
        }

        // Проверяем наличие завершенных подзадач и возвращаем соответствующий статус
        return (doneTask > 0) ? TaskStatus.DONE : TaskStatus.NEW;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();

    }
}