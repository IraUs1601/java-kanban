package service;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    void testHistoryManagerStoresTasks() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size(), "Неверное количество задач в истории.");
        Assertions.assertEquals(task1, history.get(0), "Первая задача в истории не соответствует добавленной.");
        Assertions.assertEquals(task2, history.get(1), "Вторая задача в истории не соответствует добавленной.");
    }
}