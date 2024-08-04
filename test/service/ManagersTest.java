package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ManagersTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void testManagersReturnInitializedInstances() {
        assertNotNull(taskManager, "Менеджер задач не был инициализирован.");
        assertNotNull(historyManager, "Менеджер истории не был инициализирован.");
    }

    @Test
    void testTaskManagerIntegrationWithHistoryManager() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);
        taskManager.get(task.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(task), "История должна содержать просмотренную задачу.");
    }

    @Test
    void testEpicAndSubtaskIntegrationWithHistoryManager() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(epic), "История должна содержать просмотренный эпик.");
        assertTrue(history.contains(subtask), "История должна содержать просмотренную подзадачу.");
    }

    @Test
    void testHistoryContainsTasksInOrder() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        taskManager.create(task1);
        taskManager.create(task2);

        taskManager.get(task1.getId());
        taskManager.get(task2.getId());

        List<Task> expectedHistory = List.of(task1, task2);
        List<Task> actualHistory = taskManager.getHistory();

        assertIterableEquals(expectedHistory, actualHistory, "История должна содержать задачи в правильном порядке.");
    }

    @Test
    void testArrayEquality() {
        int[] expectedArray = {1, 2, 3};
        int[] actualArray = {1, 2, 3};

        assertArrayEquals(expectedArray, actualArray, "Массивы должны быть равны.");
    }
}