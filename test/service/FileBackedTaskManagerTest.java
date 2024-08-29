package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Менеджер задач должен быть пуст после загрузки пустого файла.");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Менеджер эпиков должен быть пуст после загрузки пустого файла.");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Менеджер подзадач должен быть пуст после загрузки пустого файла.");
    }

    @Test
    void testSaveMultipleTasks() throws IOException {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Task.Status.IN_PROGRESS);
        taskManager.create(task1);
        taskManager.create(task2);

        List<String> lines = Files.readAllLines(file.toPath());
        assertFalse(lines.isEmpty(), "Файл не должен быть пустым после сохранения задач.");
        assertTrue(lines.stream().anyMatch(line -> line.contains("Задача 1")), "Файл должен содержать строку для Задачи 1.");
        assertTrue(lines.stream().anyMatch(line -> line.contains("Задача 2")), "Файл должен содержать строку для Задачи 2.");
    }
}