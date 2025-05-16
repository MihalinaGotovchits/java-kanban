package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.Task;
import structure.TaskManager;

import java.io.IOException;
import java.util.List;

public class PriorityHandler extends BaseHttpHandler implements HttpHandler {

    public PriorityHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    getPrioritizedHandler(exchange, getGson());
                default:
                    sendNotAllowed(exchange);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            exchange.close();
        }
    }

    /**
     * Метод для получения отсортированного списка задач
     */
    public void getPrioritizedHandler(HttpExchange exchange, Gson gson) throws IOException {
        List<Task> prioritizedTaskSet = getTaskManager().getPrioritizedTasks();
        if (!prioritizedTaskSet.isEmpty()) {
            String jsonString = gson.toJson(prioritizedTaskSet);
            sendText(exchange, jsonString);
        } else {
            sendNotFound(exchange);
        }
    }
}