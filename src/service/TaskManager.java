package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int currentId = 0;

    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    public void delete(int id) {
        tasks.remove(id);
    }

    public Task get(int id) {
        return tasks.get(id);
    }

    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Map<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
            epic.updateStatus();
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
        }
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.updateStatus();
            }
        }
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Map<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
        subtasks.clear();
    }

    public List<Subtask> getEpicSubtasks (int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubtasks());
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }

    private int generateId() {
        return ++currentId;
    }
}