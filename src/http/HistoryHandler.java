package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import structure.TaskManager;

import java.io.IOException;


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

