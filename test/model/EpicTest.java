package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic(1, "Эпик 1", "Описание эпика 1");
    }

    @Test
    void testAddSubtaskToEpic() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, 1);
        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(subtask, epic.getSubtasks().get(0), "Добавленная подзадача не совпадает с ожидаемой.");
    }

    @Test
    void shouldAddSubtaskToEpic() {
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, 1);
        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(subtask, epic.getSubtasks().get(0), "Добавленная подзадача не совпадает с ожидаемой.");
    }

    @Test
    void shouldCalculateEpicDurationAndTimeWhenSubtasksAdded() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, 1, Duration.ofMinutes(30), LocalDateTime.of(2024, 8, 31, 10, 0));
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.NEW, 1, Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 31, 11, 0));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateEpicFields();

        assertEquals(Duration.ofMinutes(90), epic.getDuration(), "Длительность эпика рассчитана неверно.");
        assertEquals(LocalDateTime.of(2024, 8, 31, 10, 0), epic.getStartTime(), "Начальное время эпика рассчитано неверно.");
        assertEquals(LocalDateTime.of(2024, 8, 31, 12, 0), epic.getEndTime(), "Время окончания эпика рассчитано неверно.");
    }

    @Test
    void shouldUpdateEpicStatusToNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.NEW, epic.getId());
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();
        assertEquals(Task.Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW.");
    }

    @Test
    void shouldUpdateEpicStatusToDoneWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.DONE, epic.getId());
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();
        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE.");
    }

    @Test
    void shouldUpdateEpicStatusToInProgressWhenSubtasksHaveMixedStatuses() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.DONE, epic.getId());
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    void shouldUpdateEpicStatusToInProgressWhenAllSubtasksInProgress() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.IN_PROGRESS, epic.getId());
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    void shouldRecalculateStatusWhenSubtaskRemoved() {
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Task.Status.IN_PROGRESS, epic.getId());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();
        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");

        epic.removeSubtask(subtask2);
        epic.updateStatus();

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен измениться на DONE после удаления подзадачи IN_PROGRESS.");
    }
}