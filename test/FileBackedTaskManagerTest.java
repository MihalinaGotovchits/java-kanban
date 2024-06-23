import structure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("test.csv").toFile();
        // Здесь нужно создать или скопировать содержимое в файл file
        // Например: Files.copy(sourcePath, file.toPath());

        super.taskManager = new FileBackedTaskManager(file.toPath());
        initTasks();
        taskManager.getTasksById(1);
        taskManager.getEpicTasksById(2);
        taskManager.getSubTasksById(3);
        taskManager.getSubTasksById(4);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Выводим каждую строку на консоль
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileManager.getSimpleTasks().size(), "Количество задач после выгрузки не совпадает");
        assertEquals(taskManager.getSimpleTasks(), fileManager.getSimpleTasks(), "Список задач после выгрузки не совпададает");
        assertEquals(1, fileManager.getEpicTasks().size(), "Количество эпиков после выгрузки не совпадает");
        assertEquals(taskManager.getEpicTasks(), fileManager.getEpicTasks(), "Список эпиков после выгрузки не совпадает");
        assertEquals(2, fileManager.getEpicSubTasks().size(), "Количество подзадач после выгрузки не совпадает");
        assertEquals(taskManager.getEpicSubTasks(), fileManager.getEpicSubTasks(), "Список подзадач после выгрузки не совпадает");

        List<Task> history = taskManager.getHistory();
        List<Task> historyFromFile = fileManager.getHistory();
        assertEquals(taskManager.getPrioritizedTasks(), fileManager.getPrioritizedTasks(), "Отсортированный список после выгрузки не совпадает");
        assertEquals(4, taskManager.idGenerator, "Идентификатор последней добавленной задачи после выгрузки не совпадает");
    }
}