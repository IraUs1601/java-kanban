package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {

    @Test
    void testAddDifferentTasksAndGetById() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 2");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 3", Subtask.Status.IN_PROGRESS, 2);

        taskManager.create(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Task retrievedTask = taskManager.get(1);
        Epic retrievedEpic = taskManager.getEpic(2);
        Subtask retrievedSubtask = taskManager.getSubtask(3);

        Assertions.assertEquals(task, retrievedTask, "Полученная задача не соответствует добавленной.");
        Assertions.assertEquals(epic, retrievedEpic, "Полученный эпик не соответствует добавленному.");
        Assertions.assertEquals(subtask, retrievedSubtask, "Полученная подзадача не соответствует добавленной.");
    }
}