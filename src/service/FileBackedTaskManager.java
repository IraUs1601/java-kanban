package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadTasksFromFile();
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

    private void save() {
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

    private void loadTasksFromFile() {
        System.out.println("Начало загрузки из файла: " + file.getAbsolutePath());

        int maxTasks = 1000;
        int taskCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            System.out.println("Пропущен заголовок.");
            while ((line = reader.readLine()) != null && taskCount < maxTasks) {
                System.out.println("Чтение строки: " + line);
                Task task = fromString(line);
                if (task != null) {

                    if (get(task.getId()) == null) {
                        System.out.println("Создана задача: " + task);
                        if (task instanceof Epic) {
                            createEpic((Epic) task);
                        } else if (task instanceof Subtask) {
                            createSubtask((Subtask) task);
                        } else {
                            create(task);
                        }
                        taskCount++;
                    } else {
                        System.out.println("Задача с id " + task.getId() + " уже существует.");
                    }
                } else {
                    System.out.println("Ошибка при создании задачи из строки: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            throw new ManagerSaveException("Ошибка при загрузке данных из файла.", e);
        }
        System.out.println("Загрузка из файла завершена.");
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Task.TaskType type = Task.TaskType.valueOf(parts[1]);
        String name = parts[2];
        Task.Status status = Task.Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
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
        manager.loadTasksFromFile();
        return manager;
    }
}