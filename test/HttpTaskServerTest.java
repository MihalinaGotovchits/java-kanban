import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import structure.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    static HttpServer taskServer;
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private Task task1;
    private Epic epic2;
    private Subtask subtask3;
    private Subtask subtask4;
    final LocalDateTime DATE = LocalDateTime.of(2024, Month.JULY, 10, 11, 11);
    static Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        taskServer = HttpServer.create(new InetSocketAddress(8080), 0);
        taskManager.removeSimpleTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubTasks();
        task1 = new Task("Задача", "Новая задача", 1, Status.NEW, Duration.ofMinutes(10), DATE);
        epic2 = new Epic("Эпик", "description2");
        subtask3 = new Subtask("Подзадача", "Новая подзадача", 3, Status.NEW, 2,
                Duration.ofMinutes(40), DATE.plusDays(1));
        subtask4 = new Subtask("Подзадача", "Новая подзадача", 3, Status.NEW, 2,
                Duration.ofMinutes(40), DATE.plusDays(1));
        taskManager.addSimpleTask(task1);
        taskManager.addEpicTask(epic2);
        taskManager.addSubTask(subtask3);
        taskManager.addSubTask(subtask4);
        taskManager.getEpicTasksById(2);
        taskManager.getTasksById(1);
        taskManager.getSubTasksById(3);
        taskServer.start();
        gson = new Gson();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop(0);
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(tasksList, "Список задач не получен");
        assertEquals(taskManager.getSimpleTasks(), tasksList, "Получен неверный список задач");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicsList, "Список эпиков не получен");
        assertEquals(taskManager.getEpicTasks(), epicsList, "Получен неверный список эпиков");
    }

    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getEpicSubTasks(), subtasksList, "Получен неверный список подзадач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(taskDeserialized, "Задача не получена");
        assertEquals(taskManager.getSimpleTasks().get(0), taskDeserialized, "Получена неверная задача");
    }

    @Test
    void getTaskIncorrectId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=a");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не 400");
        assertEquals("Некорректный id", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getTaskWrongId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не 404");
        assertEquals("Задача с id 3 не найдена", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicDeserialized = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicDeserialized, "Эпик не получен");
        assertEquals(taskManager.getEpicTasks().get(0), epicDeserialized, "Получен неверный эпик");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskDeserialized = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtaskDeserialized, "Подзадача не получена");
        assertEquals(taskManager.getEpicSubTasks().get(0), subtaskDeserialized, "Получена неверная подзадача");
    }

    @Test
    void getSubtasksByOneEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtask/epics/?id=2/subtaks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getListOfSubtasksByOneEpic(2), subtasksList,
                "Получен неверный список подзадач");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(history, "История не получена");
        assertEquals(3, history.size(), "Длина списка истории не 3");
        assertEquals(taskManager.getHistory().get(0).getId(), history.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(1).getId(), history.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(2).getId(), history.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080//prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> priority = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(priority, "Список приоритетных задач не получен");
        assertEquals(3, priority.size(), "Длина списка приоритетных задач не 3");
        assertEquals(taskManager.getPrioritizedTasks().get(0).getId(), priority.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(1).getId(), priority.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(2).getId(), priority.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getSimpleTasks().isEmpty(), "Задача не удалена");
        assertNull(taskManager.getTasksById(1), "Задача не удалена");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getEpicTasks().isEmpty(), "Эпик не удален");
        assertNull(taskManager.getEpicTasksById(2), "Эпик не удален");
    }

    @Test
    void removeSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertEquals(1, taskManager.getEpicSubTasks().size(), "Подзадача не удалена");
        assertNull(taskManager.getSubTasksById(3), "Подзадача не удалена");
    }

    @Test
    void removeAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getSimpleTasks().isEmpty(), "Задачи не удалены");
        assertEquals("Все задачи удалены", response.body(), "Ответ от сервера неверный");
    }

    @Test
    void removeAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getEpicTasks().isEmpty(), "Эпики не удалены");
        assertEquals("Все эпики удалены", response.body(), "Ответ от сервера неверный");
    }

    @Test
    void removeAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getEpicSubTasks().isEmpty(), "Подзадачи не удалены");
        assertEquals("Все подзадачи удалены", response.body(), "Ответ от сервера неверный");
    }

    @Test
    void addNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Задача", "description5", 5, Status.NEW, Duration.ofMinutes(1),
                LocalDateTime.of(2023, 1, 4, 0, 0));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getSimpleTasks().get(1).getId(), "Id новой задачи не совпадает");
        assertEquals(2, taskManager.getSimpleTasks().size(), "Новая задача не добавлена");
        assertEquals("Задача добавлена", response.body(), "Новая задача не добавлена");
    }

    @Test
    void addNewTaskEmptyBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не 400");
        assertEquals("Необходимо заполнить все поля задачи", response.body(), "Ответ сервера не совпадает");
    }


    @Test
    void updateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        Task task = new Task("Задача", "description1", 1, Status.IN_PROGRESS, Duration.ofMinutes(1),
                LocalDateTime.of(2023, 1, 4, 0, 0));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(1, taskManager.getSimpleTasks().get(0).getId(), "Id задачи не совпадает");
        assertEquals(1, taskManager.getSimpleTasks().size(), "Список состоит не из одной задачи");
        assertEquals("Задача обновлена", response.body(), "Задача не обновлена");
        assertEquals(task, taskManager.getTasksById(1), "Задача не обновлена");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        Epic epic5 = new Epic("Эпик", "description5");
        String json = gson.toJson(epic5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getEpicTasks().get(1).getId(), "Id нового эпика не совпадает");
        assertEquals(2, taskManager.getEpicTasks().size(), "Новый эпик не добавлен");
        assertEquals("Эпик добавлен", response.body(), "Новый эпик не добавлен");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        Epic epic2 = new Epic("Эпик", "d", 2, Status.NEW,
                LocalDateTime.of(2023, 1, 3, 0, 0), Duration.ofMinutes(2),
                LocalDateTime.of(2023, 1, 3, 16, 40));
        String json = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(2, taskManager.getEpicTasks().get(0).getId(), "Id эпика не совпадает");
        assertEquals(1, taskManager.getEpicTasks().size(), "Список состоит не из одного эпика");
        assertEquals("Эпик обновлен", response.body(), "Эпик не обновлен");
        assertEquals(epic2, taskManager.getEpicTasksById(2), "Эпик не обновлен");
    }

    @Test
    void addNewSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        Subtask subtask5 = new Subtask("Подзадача", "description5", 2, Duration.ofMinutes(1),
                LocalDateTime.of(2023, 1, 4, 0, 0));
        String json = gson.toJson(subtask5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getEpicSubTasks().get(2).getId(), "Id новой подзадачи не совпадает");
        assertEquals(3, taskManager.getEpicSubTasks().size(), "Новая подзадача не добавлена");
        assertEquals("Подзадача добавлена", response.body(), "Новая подзадача не добавлена");
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        Subtask subtask3 = new Subtask("Подзадача", "description3", 3, Status.NEW, 2,
                Duration.ofMinutes(2),
                LocalDateTime.of(2023, 1, 2, 0, 0));
        String json = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getEpicSubTasks().get(0).getId(), "Id подзадачи не совпадает");
        assertEquals(2, taskManager.getEpicSubTasks().size(), "Список состоит не из двух подзадач");
        assertEquals("Подзадача обновлена", response.body(), "Подзадача не обновлена");
        assertEquals(subtask3, taskManager.getSubTasksById(3), "Подзадача не обновлена");
    }


}