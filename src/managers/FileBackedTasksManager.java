package managers;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager  {
    private final File file;

    private static final String HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTasksManager(File file) {

        this.file = file;
    }

    private void save() {

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(HEADER);

            try {
                for (Task task : Managers.getDefault().getTaskHashMap().values()) {
                    writer.write(toString(task));
                }

                for (Epic epic : Managers.getDefault().getEpicHashMap().values()) {
                    writer.write(toString(epic));

                    for (Subtask subtask : getSubTaskList()) {
                        writer.write(toString(subtask));
                    }

                    writer.write("\n");
                    writer.write("\n" + historyToString(Managers.getDefaultHistory()) + "\n");

                }

            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }
    }

    private void fromString(String line) {
        if (line.contains("TASK") || line.contains("EPIC") || line.contains("SUBTASK")) {
            String[] params = line.split(",");
            String id = params[0];
            TaskType type = TaskType.valueOf(params[1]);
            String name = params[2];
            String taskStatus = params[3].toUpperCase(); // Преобразуем статус к верхнему регистру
            String description = params[4];
            String idOfEpic = type.equals(TaskType.SUBTASK.toString()) ? params[7] : null;

            switch (type) {
                case TASK:
                    Task task = new Task(name, description);
                    task.setId(Integer.parseInt(id));
                    task.setStatus(TaskStatus.valueOf(taskStatus));
                    tasks.put(task.getId(), task);
                    break;
                case EPIC:
                    Epic epic = new Epic(name, description, taskStatus);
                    epic.setId(Integer.parseInt(id));
                    epic.setStatus(TaskStatus.valueOf(taskStatus));
                    epics.put(epic.getId(), epic);
                    break;
                case SUBTASK:
                    Subtask subtask = new Subtask(name, description, Integer.parseInt(idOfEpic), null, null);
                    subtask.setId(Integer.parseInt(id));
                    subtask.setStatus(TaskStatus.valueOf(taskStatus));
                    subtasks.put(subtask.getId(), subtask);
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый тип задачи: " + type);
            }
        }
    }




    private static String historyToString(HistoryManager managers) {
        return managers.toString();

    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyIDS = new ArrayList<>();
        if(value != null) {
            String[] parts = value.split(",");
            for (String id : parts) {
                historyIDS.add(Integer.parseInt(id));
            }
        }
        return historyIDS;
    }

    private String toString(Task task) {
        if (task instanceof Epic epic) {
            return epic.toString();
        } else if (task instanceof Subtask subtask) {
            return subtask.toString();
        } else {
            return task.toString();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            boolean isHistorySection = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {

                    isHistorySection = true;
                    continue;
                }
                if (isHistorySection) {
                    List<Integer> history = historyFromString(line);
                    for (int taskId : history) {
                        Task task = Managers.getDefault().getTaskById(taskId);
                        if (task != null) {
                            Managers.getDefaultHistory().add(task);
                        }
                    }
                } else {

                    manager.fromString(line);
                }
            }
        } catch (IOException e) {
            throw  new RuntimeException ("Ошибка при чтении из файла: " + e.getMessage(), e);
        }

        return manager;
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File("service/resource/data.csv"));

        Task task1 = new Task("Task1", "TaskDesc1" );
        final int taskId1 = manager.addNewTask(task1);

        Epic epic1 = new Epic("Epic1", "EpicDesc1", "NEW");
        final int epicId1 = manager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "SubstackDesc1", epicId1, "2024-04-08T10:00:00", "PT2H");


        final int subtaskId2 = manager.addNewSubtask(subtask1);

        // ---Обновление задачи---------------------------------------
        Task updatedTask = manager.getTaskById(taskId1);
        updatedTask.setName("Задача №1");
        updatedTask.setDescription("Неизвестно");
        updatedTask.setStatus(TaskStatus.DONE);
        manager.updateTask(updatedTask);

        manager.getEpicById(2);
        manager.getTaskById(1);

        manager.addNewEpic(new Epic("Epic4", "Epic4 описание", "NEW"));


        System.out.println("TASKS\n" + manager.getTasks());
        System.out.println("EPICS\n" + manager.getEpics());
        System.out.println("SUBTASKS\n" + manager.getSubtasks());

        System.out.println(manager.getHistory());
    }




    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int subtaskId = super.addNewSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
        save();
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}
