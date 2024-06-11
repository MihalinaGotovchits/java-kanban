package structure;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File(String.valueOf(File.createTempFile("time", "file")));

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());

        Epic epic = new Epic("Эпик", "Новый эпик");
        Subtask subtask = new Subtask("Подзадача", "Новая подзадача", 1);
        Subtask subtask1 = new Subtask("Подзадача2", "Новая подзадача2", 1);
        Subtask subtask2 = new Subtask("Подзадача 3", "Новая подзадача 3", 1);

        fileBackedTaskManager.addEpicTask(epic);
        fileBackedTaskManager.addSubTask(subtask);
        fileBackedTaskManager.addSubTask(subtask1);
        fileBackedTaskManager.addSubTask(subtask2);

        fileBackedTaskManager.removeSubtaskById(2);
        fileBackedTaskManager.removeEpicById(1);
        System.out.println(file);
    }
}