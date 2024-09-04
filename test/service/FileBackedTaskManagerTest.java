package service;

import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @Override
    FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
            file.deleteOnExit();
        } catch (IOException e) {
            fail("Не удалось создать временный файл для теста");
        }
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @AfterEach
    public void tearDown() {
        if (file != null && !file.delete()) {
            System.err.println("Не удалось удалить временный файл: " + file.getAbsolutePath());
        }
    }

    @Test
    void testSaveAndLoadFromFile() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> loadedTasks = loadedManager.getAllTasks();

        assertEquals(task.getId(), loadedTasks.get(0).getId(), "ID задач должны совпадать");
        assertEquals(task.getName(), loadedTasks.get(0).getName(), "Имена задач должны совпадать");
        assertEquals(task.getDescription(), loadedTasks.get(0).getDescription(), "Описания задач должны совпадать");
        assertEquals(task.getStatus(), loadedTasks.get(0).getStatus(), "Статусы задач должны совпадать");
        assertEquals(task.getDuration(), loadedTasks.get(0).getDuration(), "Длительность задач должна совпадать");
    }

    @Test
    public void testSaveAndLoadEmptyFile() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Менеджер должен быть пустым");
        assertTrue(loadedManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void testExceptionWhenFileCannotBeWritten() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        if (!file.setWritable(false)) {
            throw new RuntimeException("Не удалось изменить разрешения на запись для файла: " + file.getAbsolutePath());
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        assertThrows(FileBackedTaskManager.ManagerSaveException.class,
                () -> manager.create(new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW)),
                "Ожидалось исключение ManagerSaveException при попытке сохранить данные в файл, недоступный для записи.");
    }

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        if (!file.delete()) {
            System.err.println("Не удалось удалить временный файл: " + file.getAbsolutePath());
        }

        assertThrows(FileBackedTaskManager.ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(file),
                "Ожидалось исключение ManagerSaveException при попытке загрузить данные из несуществующего файла.");
    }
}