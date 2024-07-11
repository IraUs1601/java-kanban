package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void testManagersReturnInitializedInstances() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(taskManager, "Менеджер задач не был инициализирован.");
        Assertions.assertNotNull(historyManager, "Менеджер истории не был инициализирован.");
    }
}
