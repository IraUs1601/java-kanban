import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создание задачи
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Task.Status.NEW);
        taskManager.create(task1);
        taskManager.create(task2);

        // Получение по идентификатору
        Task retrievedTask = taskManager.get(task1.getId());

        if (retrievedTask != null) {
            System.out.println("Идентификатор задачи: " + retrievedTask);
        } else {
            System.out.println("Не найдена задача с идентификатором: " + task1.getId());
        }

        // Создание эпиков
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2", Task.Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask(0, "Подзадача 3", "Описание подзадачи 3", Task.Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // Обновление статусов задач и подзадач
        updateTaskStatus(taskManager, task1, Task.Status.IN_PROGRESS);
        updateSubtaskStatus(taskManager, subtask1, Task.Status.DONE);
        updateSubtaskStatus(taskManager, subtask2, Task.Status.DONE);

        // Получение задач и добавление в историю просмотров
        taskManager.get(task1.getId());
        taskManager.get(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        // Вывод всех задач, эпиков, подзадач и истории просмотров
        printAllTasks(taskManager);

        // Удаление задачи и эпика
        taskManager.delete(task2.getId());
        taskManager.removeEpic(epic1.getId());

        // Вывод всех задач, эпиков, подзадач и истории просмотров после удаления
        printAllTasks(taskManager);
    }

    private static void updateTaskStatus(TaskManager manager, Task task, Task.Status status) {
        task.setStatus(status);
        manager.update(task);
    }

    private static void updateSubtaskStatus(TaskManager manager, Subtask subtask, Task.Status status) {
        subtask.setStatus(status);
        manager.updateSubtask(subtask);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}