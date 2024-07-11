package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.Subtask;
import structure.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


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

