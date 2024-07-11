package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.typetoken.TaskTypeToken;
import structure.TaskManager;
import structure.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    /**
     * Метод для обработки запросов
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        getAllTasksHandler(exchange, getGson());

                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        getTaskByIdHandler(exchange, getGson(), path);

                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (Pattern.matches("^/tasks$", path)) {
                        postTaskHandler(exchange, getGson(), body);

                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        Task taskChanged = getGson().fromJson(body, new TaskTypeToken().getType());
                        getTaskManager().updateTask(taskChanged);
                        sendSuccessRequest(exchange);

                    } else {
                        sendBadRequest(exchange);
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        deleteTaskHandler(exchange, path);

                    } else {
                        sendBadRequest(exchange);
                    }
                    break;

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
     * Метод для получения задачи по идентификатору
     */
    private void getTaskByIdHandler(HttpExchange exchange, Gson gson, String path) throws IOException {
        int idTask = getIdFromPath(path.replaceFirst("/tasks/", ""));
        if (idTask != -1) {
            Task gettedTask = getTaskManager().getTasksById(idTask);
            if (gettedTask == null) {
                sendNotFound(exchange);
            } else {
                String jsonString = gson.toJson(gettedTask);
                sendText(exchange, jsonString);
            }
        } else {
            sendBadRequest(exchange);
        }
    }

    /**
     * Метод для получения списка всех задач
     */
    private void getAllTasksHandler(HttpExchange exchange, Gson gson) throws IOException {
        List<Task> taskList = getTaskManager().getSimpleTasks();
        if (!taskList.isEmpty()) {
            String jsonString = gson.toJson(taskList);
            sendText(exchange, jsonString);
        } else {
            sendNotFound(exchange);
        }
    }

    /**
     * Метод для добавления задачи
     */
    private void postTaskHandler(HttpExchange exchange, Gson gson, String body) throws IOException {
        Task task = gson.fromJson(body, new TaskTypeToken().getType());
        if (getTaskManager().validate(task)) {
            getTaskManager().addSimpleTask(task);
            sendOverlap(exchange);
        } else {
            getTaskManager().addSimpleTask(task);
            sendSuccessRequest(exchange);
        }
    }

    /**
     * Метод для удаления задачи по id
     */
    private void deleteTaskHandler(HttpExchange exchange, String path) throws IOException {
        int idTask = getIdFromPath(path.replaceFirst("/tasks/", ""));
        if (idTask != -1) {
            getTaskManager().removeTaskById(idTask);
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendBadRequest(exchange);
        }
    }
}