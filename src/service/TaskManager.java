package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task create(Task task);

    void update(Task task);

    void delete(int id);

    Task get(int id);

    List<Task> getAllTasks();

    void removeAllTasks();

    void createEpic(Epic epic);

    void removeEpic(int id);

    Epic getEpic(int id);

    List<Epic> getAllEpics();

    void removeAllEpics();

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    Subtask getSubtask(int id);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}