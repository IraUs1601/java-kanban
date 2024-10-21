package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void testCreateAndGetTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);
        assertEquals(task, taskManager.get(task.getId()), "Задача должна быть успешно создана и доступна для получения.");
    }

    @Test
    void testCreateAndGetEpicAndSubtasks() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпик должен быть успешно создан и доступен для получения.");

        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Подзадача должна быть успешно создана и доступна для получения.");

        assertEquals(1, taskManager.getEpicSubtasks(epic.getId()).size(), "Количество подзадач в эпике должно соответствовать созданным.");
    }

    @Test
    void testRemoveTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);
        taskManager.delete(task.getId());
        assertNull(taskManager.get(task.getId()), "Задача должна быть удалена.");
    }

    @Test
    void testRemoveEpicAndSubtasks() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Эпик должен быть удален.");
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадачи удаленного эпика также должны быть удалены.");
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
    void testEpicStatusUpdateWithSubtasks() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Task.Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus(), "Статус эпика должен быть обновлен на 'IN_PROGRESS', если есть задачи с разными статусами.");
    }

    @Test
    void testRemoveTaskFromHistoryOnDelete() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);
        taskManager.get(task.getId());
        taskManager.delete(task.getId());

        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task), "История не должна содержать удаленную задачу.");
    }

    @Test
    void testHistoryTracking() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        taskManager.create(task1);
        taskManager.create(task2);

        taskManager.get(task1.getId());
        taskManager.get(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать все просмотренные задачи.");
        assertEquals(task1, history.get(0), "Первая задача в истории должна соответствовать задаче 1.");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна соответствовать задаче 2.");
    }

    @Test
    void testTaskOverlap() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 10, 0));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 31, 10, 30));
        taskManager.create(task1);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.create(task2),
                "Задача не должна пересекаться по времени с другой задачей.");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);

        taskManager.create(task1);
        taskManager.create(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Количество задач должно соответствовать добавленным задачам.");
        assertTrue(tasks.contains(task1), "Список задач должен содержать задачу 1.");
        assertTrue(tasks.contains(task2), "Список задач должен содержать задачу 2.");
    }

    @Test
    void testRemoveAllTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);

        taskManager.create(task1);
        taskManager.create(task2);

        taskManager.removeAllTasks();
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void testCreateNonOverlappingTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 10, 0));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 11, 0));

        taskManager.create(task1);
        taskManager.create(task2);

        assertEquals(task1, taskManager.get(task1.getId()), "Задача 1 должна быть успешно создана.");
        assertEquals(task2, taskManager.get(task2.getId()), "Задача 2 должна быть успешно создана.");
    }

    @Test
    void testCreateOverlappingTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW, Duration.ofMinutes(90), LocalDateTime.of(2024, 8, 31, 10, 0));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 10, 30));

        taskManager.create(task1);

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.create(task2),
                "Ожидалось исключение IllegalArgumentException при попытке создать пересекающуюся задачу.");

    }

    @Test
    void testUpdateTaskWithOverlap() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW, Duration.ofMinutes(90), LocalDateTime.of(2024, 8, 31, 10, 0));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 12, 0));

        taskManager.create(task1);
        taskManager.create(task2);

        task2.setStartTime(LocalDateTime.of(2024, 8, 31, 10, 30));

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.update(task2),
                "Ожидалось исключение IllegalArgumentException при попытке обновить задачу с пересекающимся временем.");
    }
}