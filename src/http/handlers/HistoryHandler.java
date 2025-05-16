package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.TaskManager;
import structure.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    getHistoryHandler(exchange, getGson());
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
     * Метод для получения истории задач
     */
    private void getHistoryHandler(HttpExchange exchange, Gson gson) throws IOException {
        List<Task> historyList = getTaskManager().getHistory();
        if (!historyList.isEmpty()) {
            String jsonString = gson.toJson(historyList);
            sendText(exchange, jsonString);
        } else {
            sendNotFound(exchange);
        }
    }
}