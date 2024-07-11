package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.typetoken.SubtaskTypeToken;
import structure.TaskManager;
import structure.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    /**
     * Метод для обработки запроса подзадач
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/subtasks$", path)) {
                        getAllSubtasksHandler(exchange, getGson());

                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        getSubtaskByIdHandler(exchange, getGson(), path);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (Pattern.matches("/subtasks$", path)) {
                        postSubtaskHandler(exchange, getGson(), body);

                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        Subtask subtaskChanged = getGson().fromJson(body, new SubtaskTypeToken().getType());
                        getTaskManager().updateSubtask(subtaskChanged);
                        sendSuccessRequest(exchange);
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        deleteSubtaskHandler(exchange, path);

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
     * Метод для получения подзадачи по id
     */
    private void getSubtaskByIdHandler(HttpExchange exchange, Gson gson, String path) throws IOException {
        int idSubtask = getIdFromPath(path.replaceFirst("/subtasks/", ""));
        if (idSubtask != -1) {
            Subtask gettedSubtask = getTaskManager().getSubTasksById(idSubtask);
            if (gettedSubtask == null) {
                sendNotFound(exchange);
            } else {
                String jsonString = gson.toJson(gettedSubtask);
                sendText(exchange, jsonString);
            }
        } else {
            sendBadRequest(exchange);
        }
    }

    /**
     * Метод для добавления подзадачи
     */
    private void postSubtaskHandler(HttpExchange exchange, Gson gson, String body) throws IOException {
        Subtask subtask = gson.fromJson(body, new SubtaskTypeToken().getType());
        if (getTaskManager().validate(subtask)) {
            getTaskManager().addSubTask(subtask);
            sendOverlap(exchange);
        } else {
            getTaskManager().addSubTask(subtask);
            sendSuccessRequest(exchange);
        }
    }

    /**
     * Метод для удаления подзадачи по id
     */
    private void deleteSubtaskHandler(HttpExchange exchange, String path) throws IOException {
        int idSubtask = getIdFromPath(path.replaceFirst("/subtasks/", ""));
        if (idSubtask != -1) {
            getTaskManager().removeSubtaskById(idSubtask);
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendBadRequest(exchange);
        }
    }

    /**
     * Метод для удаления всех подзадач
     */
    private void getAllSubtasksHandler(HttpExchange exchange, Gson gson) throws IOException {
        List<Subtask> subtaskList = getTaskManager().getEpicSubTasks();
        if (!subtaskList.isEmpty()) {
            String jsonString = gson.toJson(subtaskList);
            sendText(exchange, jsonString);
        } else {
            sendNotFound(exchange);
        }
    }
}