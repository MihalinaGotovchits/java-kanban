package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.Task;
import structure.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

