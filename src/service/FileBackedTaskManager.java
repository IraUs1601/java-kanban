package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    @Override
    public Task create(Task task) {
        Task newTask = super.create(task);
        save();
        return newTask;
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    protected void save() { // Changed to protected
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.", e);
        }
    }

    protected void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine(); // Пропустить заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // Добавить параметр -1 для предотвращения усечения
                if (parts.length < 5) continue; // Пропускать некорректные строки

                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                Task.Status status = Task.Status.valueOf(parts[3]);
                String description = parts[4];
                Integer epicId = parts.length > 5 && !parts[5].isEmpty() ? Integer.parseInt(parts[5]) : null;

                // Восстановление задач
                if (type.equals("TASK")) {
                    Task task = new Task(id, name, description, status);
                    create(task);
                } else if (type.equals("EPIC")) {
                    Epic epic = new Epic(id, name, description);
                    createEpic(epic);
                } else if (type.equals("SUBTASK")) {
                    Subtask subtask = new Subtask(id, name, description, status, epicId);
                    createSubtask(subtask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла.", e);
        }
    }

    private Task taskFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки: " + line);
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Task.Status status = Task.Status.valueOf(parts[3]);
        String description = parts[4];
        int epicId = parts.length > 5 ? Integer.parseInt(parts[5]) : -1;

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description);
            case "SUBTASK":
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,",
                    task.getId(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription());
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    task.getId(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    subtask.getEpicId());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription());
        }
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }
}