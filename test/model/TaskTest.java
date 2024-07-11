package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", Task.Status.DONE);
        Assertions.assertEquals(task1, task2, "Задачи с одинаковым идентификатором не были правильно сравнены.");
        Assertions.assertNotEquals(task1, task3, "Задачи с разными идентификаторами были ошибочно сравнены как одинаковые.");
    }
}