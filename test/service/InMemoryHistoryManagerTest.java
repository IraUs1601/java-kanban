package service;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testHistoryManagerStoresTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size(), "Неверное количество задач в истории.");
        Assertions.assertEquals(task1, history.get(0), "Первая задача в истории не соответствует добавленной.");
        Assertions.assertEquals(task2, history.get(1), "Вторая задача в истории не соответствует добавленной.");
    }

    @Test
    void testAdd() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.get(0));
    }

    @Test
    void testRemove() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task2, history.get(0));
    }

    @Test
    void testAddDuplicate() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
    }

    @Test
    void testAddMultipleTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", Task.Status.DONE);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
        Assertions.assertEquals(task3, history.get(2));
    }

    @Test
    void testRemoveNonExistentTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        historyManager.add(task1);
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.get(0));
    }
}