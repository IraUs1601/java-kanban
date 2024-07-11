package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    Task create(Task task);

    void update(Task task);

    void delete(int id);

    Task get(int id);

    Map<Integer, Task> getAllTasks();

    void removeAllTasks();

    void createEpic(Epic epic);

    void removeEpic(int id);

    Epic getEpic(int id);

    Map<Integer, Epic> getAllEpics();

    void removeAllEpics();

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    Subtask getSubtask(int id);

    Map<Integer, Subtask> getAllSubtasks();

    void removeAllSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}