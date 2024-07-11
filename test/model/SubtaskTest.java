package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Subtask(1, "Подзадача 1", "Описание подзадачи 1", Subtask.Status.NEW, 1);
        }, "Подзадача была неправильно принята как связанная с собственным эпиком.");
    }
}