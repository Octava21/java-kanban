package service;

import managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        System.out.println("--------------* СОЗДАНИЕ ЗАДАЧ: *------------------");
        Task taskToCreate = new Task("Задача №1", "Съездить за продуктами");
        final int taskId1 = manager.addNewTask(taskToCreate);

        taskToCreate = new Task("Задача №2", "Купить спортивный инвентарь");
        final int taskId2 = manager.addNewTask(taskToCreate);

        Epic epicToCreate = new Epic("Купить фруктов", "Составить список", "NEW");
        final int epicId1 = manager.addNewEpic(epicToCreate);

        Subtask subtaskToCreate = new Subtask("Яблоки", "Груша", TaskStatus.NEW, epicId1);
        final int subtaskId1 = manager.addNewSubtask(subtaskToCreate);

        subtaskToCreate = new Subtask("Фрукты для Мамы", "Виноград, черника", TaskStatus.NEW, epicId1);
        final int subtaskId2 = manager.addNewSubtask(subtaskToCreate);

        epicToCreate = new Epic("Поездка с друзьями в горы", "Список дел", "NEW");
        final int epicId2 = manager.addNewEpic(epicToCreate);

        subtaskToCreate = new Subtask("Позвонить", "Уточнить дату и список вещей", TaskStatus.NEW, epicId1);
        final int subtaskId3 = manager.addNewSubtask(subtaskToCreate);

        System.out.println("--------------*-------------------*----------------");
        // ---Обновление задачи---------------------------------------
        Task taskToUpdated = manager.getTaskById(taskId1);
        taskToUpdated.setName("Задача №1");
        taskToUpdated.setDescription("Покупка мясных продуктов");
        taskToUpdated.setStatus(TaskStatus.DONE);
        manager.updateTask(taskToUpdated);

        taskToUpdated = manager.getTaskById(taskId2);
        taskToUpdated.setName("Задача №2");
        taskToUpdated.setDescription("Купить скакалку");
        taskToUpdated.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(taskToUpdated);

        // ---Обновление первого эпика---------------------------------
        Epic epicToUpdated = manager.getEpicById(epicId1);
        manager.updateEpic(epicToUpdated);

        Subtask subtaskToUpdated = manager.getSubtaskById(subtaskId1);
        subtaskToUpdated.setDescription("Хурма, Личи");
        subtaskToUpdated.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtaskToUpdated);

        subtaskToUpdated = manager.getSubtaskById(subtaskId2);
        subtaskToUpdated.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtaskToUpdated);

        // ---Обновление второго эпика---------------------------------
        epicToUpdated = manager.getEpicById(epicId2);
        manager.updateEpic(epicToUpdated);

        subtaskToUpdated = manager.getSubtaskById(subtaskId3);
        subtaskToUpdated.setDescription("Хурма, Личи");
        subtaskToUpdated.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtaskToUpdated);

        System.out.println("--------------*-------------------*----------------");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("--------------* УДАЛЕНИЕ ЗАДАЧ: *----------------");
        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
        manager.deleteSubtaskById(3);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}