package service;

import model.Task;
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
        } catch (IOException e) {
            fail("Не удалось создать временный файл для теста");
        }
        return new FileBackedTaskManager(file);
    }

    @Test
    void testSaveAndLoadFromFile() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        taskManager.create(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> loadedTasks = loadedManager.getAllTasks();
        System.out.println("Загруженные задачи: " + loadedTasks);

        assertEquals(taskManager.getAllTasks(), loadedTasks, "Задачи должны совпадать после загрузки из файла");
    }
}