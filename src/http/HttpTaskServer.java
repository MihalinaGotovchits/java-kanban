package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import structure.*;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static HttpServer httpServer;
    private static final TaskManager manager = Managers.getDefault();


    public static void main(String[] args) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new TaskHandler(manager));
        httpServer.createContext("/epics/", new EpicHandler(manager));
        httpServer.createContext("/subtasks/", new SubtaskHandler(manager));
        httpServer.createContext("/history/", new HistoryHandler(manager));
        httpServer.createContext("/prioritized/", new PriorityHandler(manager));

        start();
        stop();
    }

    public static void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public static void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на порту " + PORT + " остановлен.");
    }
}

class TaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    String query;
    int id;
    Gson gson = new Gson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (method.equals("GET") && path.equals("/tasks/")) {
            getTasksHandler(exchange, manager);
        } else if (method.equals("GET") && path.equals("/tasks/\\d+$")) {
            getTaskByIdHandler(exchange, manager);
        } else if (method.equals("POST")) {
            postTaskHandler(exchange, manager);
        } else if (method.equals("DELETE") && path.equals("/tasks/\\d+$")) {
            deleteTaskByIdHandler(exchange, manager);
        } else if (method.equals("DELETE")) {
            deleteTasksHandler(exchange, manager);
        } else {
            System.out.println("Неизвестный метод");
        }
    }

    public void getTaskByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            sendNotFound(exchange, "Некорректный id");
            return;
        }
        Task task = manager.getTasksById(id);
        if (task != null) {
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange, "Задача с id " + id + " не найдена");
        }
    }

    public void getTasksHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        sendText(exchange, gson.toJson(manager.getSimpleTasks()));
    }

    public void postTaskHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        InputStream inputStreamTask = exchange.getRequestBody();
        String bodyTask = new String(inputStreamTask.readAllBytes(), StandardCharsets.UTF_8);
        if (bodyTask.isEmpty()) {
            writeResponse(exchange, "Необходимо заполнить все поля задачи", 400);
            return;
        }
        try {
            Task task = gson.fromJson(bodyTask, Task.class);
            if (task.getId() == 0) {
                manager.addSimpleTask(task);
                writeResponse(exchange, "Задача добавлена", 201);
            } else {
                manager.updateTask(task);
                writeResponse(exchange, "Задача обновлена", 201);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    public void deleteTaskByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        if (manager.getTasksById(id) != null) {
            manager.removeTaskById(id);
            sendText(exchange, "Задача удалена");
        } else {
            sendNotFound(exchange, "Задача с id " + id + " не найдена");
        }
    }

    public void deleteTasksHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        manager.removeSimpleTasks();
        if (manager.getSimpleTasks().isEmpty()) {
            sendText(exchange, "Все задачи удалены");
        }
    }
}

class EpicHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    String query;
    int id;
    Gson gson = new Gson();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (method.equals("GET") && path.equals("/epics")) {
            getEpicsHandler(exchange, manager);
        } else if (method.equals("GET") && path.equals("/epics/\\d+$")) {
            getEpicByIdHandler(exchange, manager);
        } else if (method.equals("GET") && path.equals("/epics/\\d+/subtasks$")) {
            getEpicsSubtasksHandler(exchange, manager);
        } else if (method.equals("POST") && path.equals("/epics")) {
            postEpicHandler(exchange, manager);
        } else if (method.equals("DELETE") && path.equals("/epics")) {
            deleteEpics(exchange, manager);
        } else if (method.equals("DELETE") && path.equals("epics/\\d+$")) {
            deleteEpicByIdHandler(exchange, manager);
        } else {
            System.out.println("Неизвестный метод");
        }
    }

    public void getEpicByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        Epic epic = manager.getEpicTasksById(id);
        if (epic != null) {
            sendText(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange, "Эпик с id " + id + " не найден");
        }
    }

    public void getEpicsHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        sendText(exchange, gson.toJson(manager.getEpicTasks()));
    }

    public void getEpicsSubtasksHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        if (manager.getEpicTasksById(id) != null) {
            sendText(exchange, gson.toJson(manager.getListOfSubtasksByOneEpic(id)));
        } else {
            sendNotFound(exchange, "Список подзадач эпика с id " + id + " не найден");
        }
    }

    public void postEpicHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        InputStream inputStreamEpic = exchange.getRequestBody();
        String bodyEpic = new String(inputStreamEpic.readAllBytes(), StandardCharsets.UTF_8);
        if (bodyEpic.isEmpty()) {
            writeResponse(exchange, "Необходимо заполнить все поля эпика", 400);
            return;
        }
        try {
            Epic epic = gson.fromJson(bodyEpic, Epic.class);
            if (epic.getId() == 0) {
                manager.addEpicTask(epic);
                writeResponse(exchange, "Эпик добавлен", 201);
            } else {
                manager.updateEpic(epic);
                writeResponse(exchange, "Эпик обновлен", 201);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    public void deleteEpicByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        if (manager.getEpicTasksById(id) != null) {
            manager.removeEpicById(id);
            sendText(exchange, "Эпик удален");
        } else {
            sendNotFound(exchange, "Эпик с id " + id + " не найден");
        }
    }

    public void deleteEpics(HttpExchange exchange, TaskManager manager) throws IOException {
        manager.removeAllEpics();
        if (manager.getEpicTasks().isEmpty()) {
            sendText(exchange, "Все эпики удалены");
        }
    }
}

class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    String query;
    int id;
    Gson gson = new Gson();

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (method.equals("GET") && path.equals("/subtasks")) {
            getSubtasksHandler(exchange, manager);
        } else if (method.equals("GET") && path.equals("/subtasks/\\d+$")) {
            getSubtaskByIdHandler(exchange, manager);
        } else if (method.equals("POST") && path.equals("/subtasks")) {
            postSubtaskHandler(exchange, manager);
        } else if (method.equals("DELETE") && path.equals("/subtasks")) {
            deleteSubtasksHandler(exchange, manager);
        } else if (method.equals("DELETE") && path.equals("/subtasks/\\d+$")) {
            deleteSubtaskByIdHandler(exchange, manager);
        } else {
            System.out.println("Неизвестный метод");
        }
    }

    public void getSubtaskByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        Subtask subtask = manager.getSubTasksById(id);
        if (subtask != null) {
            sendText(exchange, gson.toJson(subtask));
        } else {
            sendNotFound(exchange, "Подзадача с id " + id + " не найдена");
        }
    }

    public void getSubtasksHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        sendText(exchange, gson.toJson(manager.getEpicSubTasks()));
    }

    public void postSubtaskHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        InputStream inputStreamSubtask = exchange.getRequestBody();
        String bodySubtask = new String(inputStreamSubtask.readAllBytes(), StandardCharsets.UTF_8);
        if (bodySubtask.isEmpty()) {
            writeResponse(exchange, "Необходимо заполнить все поля задачи", 400);
            return;
        }
        try {
            Subtask subtask = gson.fromJson(bodySubtask, Subtask.class);
            if (subtask.getId() == 0) {
                manager.addSubTask(subtask);
                writeResponse(exchange, "Подзадача добавлена", 201);
            } else {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача обновлена", 201);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    public void deleteSubtaskByIdHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        query = exchange.getRequestURI().getQuery();
        id = getId(query);
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", 400);
            return;
        }
        if (manager.getSubTasksById(id) != null) {
            manager.removeSubtaskById(id);
            sendText(exchange, "Подзадача удалена");
        } else {
            sendNotFound(exchange, "Подзадача с id " + id + " не найдена");
        }
    }

    public void deleteSubtasksHandler(HttpExchange exchange, TaskManager manager) throws IOException {
        manager.removeAllSubTasks();
        if (manager.getEpicSubTasks().isEmpty()) {
            sendText(exchange, "Все подзадачи удалены");
        }
    }
}

class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson = new Gson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getHistory()));
    }
}

class PriorityHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson = new Gson();

    public PriorityHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
    }
}

class BaseHttpHandler {
    protected void sendText(HttpExchange t, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        t.sendResponseHeaders(200, resp.length);
        t.getResponseBody().write(resp);
        t.close();
    }

    protected void sendNotFound(HttpExchange t, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        t.sendResponseHeaders(404, resp.length);
        t.getResponseBody().write(resp);
        t.close();
    }

    protected void sendHasInteractions(HttpExchange t, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        t.sendResponseHeaders(406, resp.length);
        t.getResponseBody().write(resp);
        t.close();
    }

    protected void writeResponse(HttpExchange t, String response, int code) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
        t.close();
    }

    public int getId(String query) {
        try {
            return Optional.of(Integer.parseInt(query.replaceFirst("id=", ""))).get();
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}