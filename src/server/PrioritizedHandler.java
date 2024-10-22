package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;  // Используем переданный Gson
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = gson.toJson(manager.getAllTasks());
                sendResponse(exchange, 200, response);
            } else {
                sendResponseWithoutBody(exchange, 405);
            }
        } catch (Exception e) {
            sendResponseWithoutBody(exchange, 500);
        }
    }
}