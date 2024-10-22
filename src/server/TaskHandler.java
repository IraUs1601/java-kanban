package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Class<?> taskType;
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(Class<?> taskType, TaskManager manager, Gson gson) {
        this.taskType = taskType;
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteTask(exchange);
                    break;
                default:
                    sendResponseWithoutBody(exchange, 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseWithoutBody(exchange, 500);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response;
        if (taskType == Task.class) {
            response = gson.toJson(manager.getAllTasks());
        } else if (taskType == Subtask.class) {
            response = gson.toJson(manager.getAllSubtasks());
        } else {
            response = gson.toJson(manager.getAllEpics());
        }
        sendResponse(exchange, 200, response);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Object task = gson.fromJson(reader, taskType);

        if (task == null) {
            sendResponseWithoutBody(exchange, 400);
            return;
        }

        if (taskType == Task.class) {
            handleTaskCreationOrUpdate((Task) task, exchange);
        } else if (taskType == Subtask.class) {
            handleSubtaskCreationOrUpdate((Subtask) task, exchange);
        } else if (taskType == Epic.class) {
            handleEpicCreation(exchange, (Epic) task);
        } else {
            sendResponseWithoutBody(exchange, 400);
        }
    }

    private void handleTaskCreationOrUpdate(Task task, HttpExchange exchange) throws IOException {
        if (task.getId() == 0) {
            manager.create(task);
            sendResponseWithoutBody(exchange, 201);
        } else if (manager.get(task.getId()) != null) {
            manager.update(task);
            sendResponseWithoutBody(exchange, 200);
        } else {
            sendResponseWithoutBody(exchange, 404);
        }
    }

    private void handleSubtaskCreationOrUpdate(Subtask subtask, HttpExchange exchange) throws IOException {
        if (subtask.getId() == 0) {
            manager.createSubtask(subtask);
            sendResponseWithoutBody(exchange, 201);
        } else if (manager.getSubtask(subtask.getId()) != null) {
            manager.updateSubtask(subtask);
            sendResponseWithoutBody(exchange, 200);
        } else {
            sendResponseWithoutBody(exchange, 404);
        }
    }

    private void handleEpicCreation(HttpExchange exchange, Epic epic) throws IOException {
        if (epic.getId() == 0) {
            manager.createEpic(epic);
            sendResponseWithoutBody(exchange, 201);
        } else {
            sendResponseWithoutBody(exchange, 400);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.startsWith("id=")) {
            try {
                int id = Integer.parseInt(query.substring(3));

                if (taskType == Task.class) {
                    Task task = manager.get(id);
                    if (task != null) {
                        manager.delete(id);
                        sendResponseWithoutBody(exchange, 200);
                    } else {
                        sendResponse(exchange, 404, "Задача с id " + id + " не найдена.");
                    }
                } else if (taskType == Subtask.class) {
                    Subtask subtask = manager.getSubtask(id);
                    if (subtask != null) {
                        manager.removeSubtask(id);
                        sendResponseWithoutBody(exchange, 200);
                    } else {
                        sendResponse(exchange, 404, "Подзадача с id " + id + " не найдена.");
                    }
                } else if (taskType == Epic.class) {
                    Epic epic = manager.getEpic(id);
                    if (epic != null) {
                        manager.removeEpic(id);
                        sendResponseWithoutBody(exchange, 200);
                    } else {
                        sendResponse(exchange, 404, "Эпик с id " + id + " не найдена.");
                    }
                } else {
                    sendResponse(exchange, 400, "Неизвестный тип задачи.");
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат id. Id должен быть числом.");
            }
        } else {
            sendResponse(exchange, 400, "Параметр id отсутствует или недействителен.");
        }
    }
}