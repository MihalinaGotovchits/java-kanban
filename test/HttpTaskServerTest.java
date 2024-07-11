import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import http.HttpTaskServer;
import http.typetoken.EpicListTypeToken;
import http.typetoken.SubtaskListTypeToken;
import http.typetoken.TaskListTypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HttpTaskServerTest {

    HttpTaskServer server = new HttpTaskServer();

    HttpTaskServerTest() throws IOException {
    }


    @BeforeEach
    void setUp() throws ManagerSaveException {

        server.start();
        TaskManager manager = server.getTaskManager();
        manager.removeSimpleTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Задача",
                "Описание Задачи",
                3,
                Status.NEW,
                Duration.ofMinutes(25L),
                LocalDateTime.now()
        );

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/tasks");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseTask.statusCode());
        List<Task> taskList = manager.getSimpleTasks();
        assertNotNull(taskList, "Tasks: List is empty");
        assertEquals(task.getName(), taskList.get(0).getName(), "Tasks: Not equal");
    }

    @Test
    void shouldIntersection() throws IOException, InterruptedException {
        Task task = new Task("Задача",
                "Первая задача",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 12, 00),
                Duration.ofMinutes(15L));
        Task taskTwo = new Task("Задача 2",
                "Вторая задача",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 12, 10),
                Duration.ofMinutes(15L));
        Task taskThree = new Task("Задача 3",
                "Третья задача",
                1,
                Status.NEW,
                Duration.ofMinutes(35L),
                LocalDateTime.now());

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addSimpleTask(task);

        String taskJson = gson.toJson(taskTwo);
        String taskThreeJson = gson.toJson(taskThree);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/tasks");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpRequest requestThree = HttpRequest.newBuilder()
                .uri(uriTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskThreeJson))
                .build();

        HttpResponse<String> responseTask = client.send(requestThree, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseTask.statusCode(), "Задачи не пересекаются");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача",
                "Описание задачи",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 12, 00),
                Duration.ofMinutes(15L));
        task.setId(1);
        Task taskTwo = new Task("Задача 2",
                "Вторая задача",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 19, 00),
                Duration.ofMinutes(15L));
        taskTwo.setId(2);

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addSimpleTask(task);
        manager.addSimpleTask(taskTwo);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/tasks");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTask.statusCode());
        String body = responseTask.body();
        List<Task> jsonTask = gson.fromJson(body, new TaskListTypeToken().getType());
        assertEquals(task, jsonTask.get(0));
        assertEquals(manager.getSimpleTasks(), jsonTask);
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик",
                "Новый эпик",
                1,
                Status.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        Subtask subtask = new Subtask("Задача",
                "Новая задача",
                1,
                Status.NEW,
                1,
                Duration.ofMinutes(25L),
                LocalDateTime.now());

        TaskManager manager = server.getTaskManager();
        manager.addEpicTask(epic);
        Gson gson = server.setGson();

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseTask.statusCode());
        List<Subtask> taskList = manager.getEpicSubTasks();
        assertNotNull(taskList, "Tasks: Список пуст");
        assertEquals(subtask.getName(), taskList.get(0).getName(), "Tasks: Not equal");
    }

    @Test
    void TestGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик",
                "Новый эпик",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 12, 00),
                Duration.ofMinutes(15L),
                LocalDateTime.of(2024, 01, 01, 12, 15));
        epic.setId(1);

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addEpicTask(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/epics");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTask.statusCode());
        String body = responseTask.body();
        List<Epic> jsonSub = gson.fromJson(body, new EpicListTypeToken().getType());
        assertEquals(manager.getEpicTasks(), jsonSub);
    }

    @Test
    void TestGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик",
                "Новый эпик",
                1,
                Status.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        Subtask subOne = new Subtask("Подзадача",
                "Первая подзадача",
                Status.NEW,
                1,
                Duration.ofMinutes(15L),
                LocalDateTime.of(2024, 01, 01, 12, 00));
        subOne.setId(2);
        Subtask subTwo = new Subtask("Подзадача 2",
                "Вторая подзадача",
                Status.NEW,
                1,
                Duration.ofMinutes(15L),
                LocalDateTime.of(2024, 01, 01, 19, 00));
        subTwo.setId(3);

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addEpicTask(epic);
        manager.addSubTask(subOne);
        manager.addSubTask(subTwo);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTask.statusCode());
        String body = responseTask.body();
        List<Subtask> jsonSub = gson.fromJson(body, new SubtaskListTypeToken().getType());
        assertEquals(manager.getEpicSubTasks(), jsonSub);
        //client.close();
    }

    @Test
    void TestGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача",
                "Описание задачи",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(25L));

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addSimpleTask(task);
        manager.getTasksById(1);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/history");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        List<Task> history = manager.getHistory();
        List<Task> jsonResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(history, jsonResponse);
        //client.close();
    }

    @Test
    void testGetPriority() throws IOException, InterruptedException {
        Task task = new Task("Задача",
                "Описание задачи",
                Status.NEW,
                LocalDateTime.of(2024, 01, 01, 12, 00),
                Duration.ofMinutes(15L));
        Task taskTwo = new Task("Задача 2",
                "Вторая задача",
                Status.NEW,
                LocalDateTime.of(2024, 01, 02, 12, 10),
                Duration.ofMinutes(15L));

        TaskManager manager = server.getTaskManager();
        Gson gson = server.setGson();
        manager.addSimpleTask(task);
        manager.addSimpleTask(taskTwo);

        HttpClient client = HttpClient.newHttpClient();
        URI uriTask = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        List<Task> treeManager = manager.getPrioritizedTasks();
        List<Task> treeGson = gson.fromJson(responseTask.body(), new TaskListTypeToken().getType());
        assertEquals(treeManager, treeGson);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}
