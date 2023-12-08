package service.schedule;

import model.schedule.Epic;
import model.schedule.Subtask;
import model.schedule.Task;
import model.schedule.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

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
        subtasks.put(subtask.getId() , subtask);
        epics.get(subtask.getEpicId()).addSubTaskId(subtask.getId());
        return subtask.getId();
    }

    // Получение списка task
    public ArrayList<Task> getTasks() {
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

    // Удаление задач/подзадач/эпиков

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
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
                calculateEpicStatus(epic.getId());
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
        if (!epicSubtaskIds.isEmpty()) {
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
                        inProgress++;
                        break;
                    // Добавьте другие статусы при необходимости
                    default:
                        break;
                }
            }
        } else {
            return TaskStatus.NEW;
        }
        if (newTask > 0 && inProgress == 0 && doneTask == 0) {
            return TaskStatus.NEW;
        }
        if (inProgress > 0) {
            return TaskStatus.IN_PROGRESS;
        }
        if (doneTask > 0 && inProgress == 0 && newTask == 0) {
            return TaskStatus.DONE;
        }
        if (doneTask > 0 && inProgress == 0 && newTask > 0) {
            return TaskStatus.IN_PROGRESS;
        }
        return TaskStatus.NEW;
    }

}














