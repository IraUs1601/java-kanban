package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", Task.Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковым идентификатором не были правильно сравнены.");
        assertNotEquals(task1, task3, "Задачи с разными идентификаторами были ошибочно сравнены как одинаковые.");
    }

    @Test
    void testTaskEndTimeCalculation() {
        LocalDateTime startTime = LocalDateTime.of(2024, 8, 31, 10, 0);
        Duration duration = Duration.ofMinutes(90);
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW, duration, startTime);

        assertEquals(startTime.plus(duration), task.getEndTime(), "Время окончания задачи рассчитано неверно.");
    }
}