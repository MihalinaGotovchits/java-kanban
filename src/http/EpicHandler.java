package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.Epic;
import structure.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


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

