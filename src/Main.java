import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задачи
        Task task1 = new Task(taskManager.generateId(), "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(taskManager.generateId(), "Задача 2", "Описание задачи 2", Task.Status.NEW);
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
        Epic epic1 = new Epic(taskManager.generateId(), "Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic(taskManager.generateId(), "Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask(taskManager.generateId(), "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(taskManager.generateId(), "Подзадача 2", "Описание подзадачи 2", Task.Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask(taskManager.generateId(), "Подзадача 3", "Описание подзадачи 3", Task.Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // Вывод списка задач, эпиков и подзадач
        System.out.println("Список задач: " + taskManager.getAllTasks());
        System.out.println("Список эпиков: " + taskManager.getAllEpics());
        System.out.println("Список подзадач: " + taskManager.getAllSubtasks());

        // Обновление статусов
        task1.setStatus(Task.Status.IN_PROGRESS);
        taskManager.update(task1);
        subtask1.setStatus(Task.Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Task.Status.DONE);
        taskManager.updateSubtask(subtask2);

        // Проверка обновления статусов
        System.out.println("Список задач после обновления статусов: " + taskManager.getAllTasks());
        System.out.println("Список эпиков после обновления статусов: " + taskManager.getAllEpics());
        System.out.println("Список подзадач после обновления статусов: " + taskManager.getAllSubtasks());

        // Удаление задачи и эпика
        taskManager.delete(task2.getId());
        taskManager.removeEpic(epic1.getId());

        // Вывод списка задач, эпиков и подзадач после удаления
        System.out.println("Список задач после удаления: " + taskManager.getAllTasks());
        System.out.println("Список эпиков после удаления: " + taskManager.getAllEpics());
        System.out.println("Список подзадач после удаления: " + taskManager.getAllSubtasks());
    }
}