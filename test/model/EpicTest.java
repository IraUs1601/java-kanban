package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void testAddSubtaskToEpic() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Task.Status.NEW, 1);
        epic.addSubtask(subtask);

        Assertions.assertEquals(1, epic.getSubtasks().size(), "Неверное количество подзадач в эпике.");
        Assertions.assertEquals(subtask, epic.getSubtasks().get(0), "Добавленная подзадача не совпадает с ожидаемой.");
    }
}