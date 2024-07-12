package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddDifferentTasksAndGetById() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 2");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 3", Subtask.Status.IN_PROGRESS, 2);

        taskManager.create(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Task retrievedTask = taskManager.get(1);
        Epic retrievedEpic = taskManager.getEpic(2);
        Subtask retrievedSubtask = taskManager.getSubtask(3);

        assertEquals(task, retrievedTask, "Полученная задача не соответствует добавленной.");
        assertEquals(epic, retrievedEpic, "Полученный эпик не соответствует добавленному.");
        assertEquals(subtask, retrievedSubtask, "Полученная подзадача не соответствует добавленной.");
    }

    @Test
    void testAddAndGetTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);

        Task retrievedTask = taskManager.get(1);
        assertEquals(task, retrievedTask, "Полученная задача не соответствует добавленной.");
    }
    @Test
    void testAddAndRemoveEpic() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);

        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Эпик должен быть удален.");
    }

    @Test
    void testAddAndRemoveSubtask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        taskManager.removeSubtask(subtask.getId());
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена.");
        assertTrue(taskManager.getEpic(epic.getId()).getSubtasks().isEmpty(), "Эпик не должен содержать удаленные подзадачи.");
    }

    @Test
    void testUpdateTaskStatus() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);

        task.setStatus(Task.Status.IN_PROGRESS);
        taskManager.update(task);

        Task updatedTask = taskManager.get(task.getId());
        assertEquals(Task.Status.IN_PROGRESS, updatedTask.getStatus(), "Статус задачи должен быть обновлен.");
    }

    @Test
    void testDataIntegrityOnEpicAndSubtaskRemoval() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        taskManager.removeSubtask(subtask.getId());
        assertTrue(taskManager.getEpic(epic.getId()).getSubtasks().isEmpty(), "Эпик не должен содержать удаленные подзадачи.");

        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадачи удаленного эпика не должны существовать.");
    }

    @Test
    void testUpdateSubtaskStatus() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        subtask.setStatus(Task.Status.DONE);
        taskManager.updateSubtask(subtask);

        Subtask updatedSubtask = taskManager.getSubtask(subtask.getId());
        assertEquals(Task.Status.DONE, updatedSubtask.getStatus(), "Статус подзадачи должен быть обновлен.");
    }

    @Test
    void testRemoveTaskFromHistoryOnDelete() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);
        taskManager.get(task.getId());  // Добавляем в историю
        taskManager.delete(task.getId());

        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task), "История не должна содержать удаленную задачу.");
    }
}