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


