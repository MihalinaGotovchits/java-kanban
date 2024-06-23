package structure;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File(String.valueOf(File.createTempFile("time", "file")));

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());

        Epic epic = new Epic("Эпик", "Новый эпик");
        Subtask subtask = new Subtask("Подзадача", "Новая подзадача", 1, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JUNE, 20, 11, 41));
        Subtask subtask1 = new Subtask("Подзадача2", "Новая подзадача2", 1, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JUNE, 20, 12, 41));
        Subtask subtask2 = new Subtask("Подзадача 3", "Новая подзадача 3", 1,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JUNE, 21, 10, 45));

        /*fileBackedTaskManager.addEpicTask(epic);
        fileBackedTaskManager.addSubTask(subtask);
        fileBackedTaskManager.addSubTask(subtask1);
        fileBackedTaskManager.addSubTask(subtask2);
        System.out.println(fileBackedTaskManager.getEpicTasks());
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());

        fileBackedTaskManager.removeSubtaskById(2);
        fileBackedTaskManager.removeEpicById(1);
        System.out.println(file);*/

        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpicTask(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask1);
        taskManager.addSimpleTask(subtask2);
        //System.out.println(taskManager.getEpicSubTasks());
        System.out.println(taskManager.getPrioritizedTasks());
        taskManager.removeAllEpics();
        System.out.println(taskManager.getEpicTasks());
    }
}