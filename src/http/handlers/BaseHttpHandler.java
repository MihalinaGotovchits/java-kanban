package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import structure.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


class BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.taskManager = manager;
        this.gson = gson;
    }

    public Gson getGson() {
        return this.gson;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    /**
     * Метод для отправки ответа в случае успешного выполнения запроса
     */
    public void sendText(HttpExchange t, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        t.sendResponseHeaders(200, resp.length);
        t.getResponseBody().write(resp);
        t.close();
    }

    /**
     * Метод для отправки сообщения, в случае, если в запросе есть ошибки
     */
    public void sendBadRequest(HttpExchange exchange) throws IOException {
        byte[] response = ("Bad request").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(400, response.length);
        exchange.getResponseBody().write(response);
    }

    /**
     * Метод для отправки ответа в случае, если объект не найден
     */
    public void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] response = ("Object not found").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(404, response.length);
        exchange.getResponseBody().write(response);
    }

    /**
     * Метод для отправки ответа в случае, если метод запроса неподдерживаемый
     */
    public void sendNotAllowed(HttpExchange exchange) throws IOException {
        byte[] response = ("Method not allowed").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(405, response.length);
        exchange.getResponseBody().write(response);
    }

    /**
     * Метод для отправки ответа в случае, если добавляемая задача/подзадача пересекается с уже имеющимися в
     * менеджере задачами/подзадачами
     */
    public void sendOverlap(HttpExchange exchange) throws IOException {
        byte[] response = ("Tasks overlap").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(406, response.length);
        exchange.getResponseBody().write(response);
    }

    /**
     * Метод для отправки ответа об успешном выполнении запроса без возвращения тела запроса
     */
    public void sendSuccessRequest(HttpExchange exchange) throws IOException {
        byte[] response = ("Success").getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(201, response.length);
        exchange.getResponseBody().write(response);
    }

    /**
     * Метод для получения идентификатора задачи из пути запроса
     */
    public int getIdFromPath(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}