package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


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