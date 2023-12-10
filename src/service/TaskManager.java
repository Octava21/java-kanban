package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 0;


    // Добавление новой task

    public int addNewTask(Task task) {
        task.setId(++nextId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int addNewEpic(Epic epic) {
        epic.setId(++nextId);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public int addNewSubtask(Subtask subtask) {
        subtask.setId(++nextId);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getId());
            calculateEpicStatus(epic.getId());
        }

        return subtask.getId();
    }


    // Получение списка task
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }


    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение task по ID

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Удаление всех списка всех задач (Задача/Эпик/Подзадача)

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    // ОБНОВЛЕНИЕ ЗАДАЧ/ПОДЗАДАЧ/ЭПИКОВ

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
        }
    }

    public void updateTask(Task task){
        tasks.put(task.getId(),task);
    }

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

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            subtasks.remove(id);
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                getSubtasks().remove(subtask);
                calculateEpicStatus(epic.getId());
            }
        }
    }


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

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
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
}