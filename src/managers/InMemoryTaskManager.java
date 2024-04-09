package managers;

import exceptions.TimeIntersectionException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {
    public final Map<Integer, Task> tasks = new HashMap<>();
    public final Map<Integer, Epic> epics = new HashMap<>();
    public final Map<Integer, Subtask> subtasks = new HashMap<>();
    public final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 0;
    protected TreeSet<Task> prioritiziedTasks = new TreeSet<>((Task o1, Task o2) -> {
        if (o1.getStartTime() != null && o2.getStartTime() != null) {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().equals(o2.getStartTime())) {
                return -1;
            }
        } else if (o1.getStartTime() == null && o2.getStartTime() != null) {
            return 1;
        } else if (o1.getStartTime() != null && o2.getStartTime() == null) {
            return -1;
        }
        return -1;
    });

    // Добавление новой task
    @Override
    public int addNewTask(Task task) {
        if (!timeIntersectionCheck(task, this.prioritiziedTasks)) {
            throw new TimeIntersectionException("Таска не создана из-за пересечения по времени");
        }

        task.setId(++nextId);
        tasks.put(task.getId(), task);
        prioritiziedTasks.add(task); // Добавляем задачу в приоритетизированный список
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

        if (!timeIntersectionCheck(subtask, this.prioritiziedTasks)) {
            throw new TimeIntersectionException("Подзадача не создана из-за пересечения по времени");
        }

        subtask.setId(++nextId);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subtask.getId());
            TaskStatus status = calculateEpicStatus(epic.getId());
            epicDurationUpdater(epic, subtasks); // Вызываем метод для обновления длительности эпика
        }

        prioritiziedTasks.add(subtask); // Добавляем подзадачу в приоритетизированный список
        return subtask.getId();
    }

    @Override
    public Map<Integer, Task> getTaskHashMap() {
        return null;
    }

    @Override
    public Map<Integer, Epic> getEpicHashMap() {
        return null;
    }

    @Override
    public List<Subtask> getSubTaskList() {
        return null;
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
                epic.setStatus(TaskStatus.valueOf(status.name()));
                epicDurationUpdater(epic, subtasks);
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
                epicDurationUpdater(epic, subtasks);

            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            // Получаем список подзадач эпика
            List<Subtask> epicSubtasks = getSubtasksByEpicId(id);

            // Удаляем каждую подзадачу эпика
            for (Subtask subtask : epicSubtasks) {
                deleteSubtaskById(subtask.getId());
            }

            // Удаляем сам эпик из коллекции эпиков
            epics.remove(id);

            // Добавляем удаление эпика в историю
            historyManager.add(epic);
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritiziedTasks;
    }





    // Расчет статуса эпика по id
    private TaskStatus calculateEpicStatus(int epicId) {
        ArrayList<Integer> epicSubtaskIds = epics.get(epicId).getSubTaskId();
        int newTask = 0;
        int inProgress = 0;
        int doneTask = 0;

        for (int subtaskId : epicSubtaskIds) {
            TaskStatus subtaskStatus = TaskStatus.valueOf(String.valueOf(subtasks.get(subtaskId).getStatus()));

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


    private boolean timeIntersectionCheck(Task task, Collection<? extends Task> tasksTreeSet) {
        if (task.getStartTime() != null) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();

            for (Task taskFromSet : tasksTreeSet) {
                LocalDateTime startTime = taskFromSet.getStartTime();
                LocalDateTime endTime = taskFromSet.getEndTime();

                if ((startTime != null && startTime.isBefore(taskEndTime) && endTime != null && endTime.isAfter(taskStartTime)) ||
                        (startTime != null && startTime.isEqual(taskEndTime)) || (endTime != null && endTime.isEqual(taskStartTime))) {
                    return true;
                }
            }
        }
        return false;
    }
    private void epicDurationUpdater(Epic epic, Map<Integer, Subtask> subtaskMap) {
        if (subtaskMap.containsKey(epic.getSubTaskId().stream().findFirst())) {

            LocalDateTime epicStartTime = Objects.requireNonNull(epic.getSubTaskId().stream()
                    .map(subtaskMap::get)
                    .min(Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .stream().findFirst().orElse(null)).getStartTime();

            if (epicStartTime != null) {
                LocalDateTime epicEndTime = Objects.requireNonNull(epic.getSubTaskId().stream()
                        .map(subtaskMap::get)
                        .max(Comparator.comparing(Task::getEndTime))
                        .stream().findFirst().orElse(null)).getEndTime();

                epic.setStartTime(epicStartTime);
                epic.setDuration(Duration.between(epicStartTime, epicEndTime));
            } else {
                epic.setStartTime(null);
                epic.setDuration(null);
            }
        }
    }

    private void epicStatusUpdater(Epic epic, Map<Integer, Subtask> subtaskMap) {
        Collection<Integer> listOfSubTasks = epic.getSubTaskId();
        int counterSameStatus = 0;
        TaskStatus firstSubtaskStatus = null;

        for (Integer idSubtask : listOfSubTasks) {
            Subtask subtask = getSubtaskById(idSubtask);
            TaskStatus currentStatus = subtask.getStatus();

            if (firstSubtaskStatus == null) {
                firstSubtaskStatus = currentStatus;
            }

            if (!firstSubtaskStatus.equals(currentStatus)) {
                epic.setStatus(TaskStatus.IN_PROGRESS); // Устанавливаем статус эпика IN_PROGRESS
                return; // Выходим из цикла
            } else {
                counterSameStatus++;
            }
        }

        // Если все подзадачи имеют одинаковый статус, устанавливаем этот статус для эпика
        if (counterSameStatus == listOfSubTasks.size()) {
            epic.setStatus(firstSubtaskStatus);
        }
    }

}