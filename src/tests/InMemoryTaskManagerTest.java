package tests;

import Structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest<T extends TaskManager> {
    protected T taskManager = (T) new InMemoryTaskManager();
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void initTasks() {
        task = new Task("Задача", "Новая задача");
        taskManager.addSimpleTask(task);
        epic = new Epic("Эпик", "Новый эпик");
        taskManager.addEpicTask(epic);
        subtask1 = new Subtask("Подзадача1", "Новая подзадача 1", 2);
        taskManager.addSubTask(subtask1);
        subtask2 = new Subtask("Подзадача2", "Новая подзадача 2", 2);
        taskManager.addSubTask(subtask2);
    }

    @Test
    void addSimpleTask() {
        Task expectedTask = taskManager.getTasksById(1);

        Assertions.assertNotNull(expectedTask, "Задача не найдена");
        Assertions.assertEquals(expectedTask, task, "Задачи не совпадают");

        final ArrayList<Task> expectedTasks = taskManager.getSimpleTasks();

        assertNotNull(expectedTasks, "Задачи не возвращаются");
        assertEquals(1, expectedTasks.size(), "Неверное количество задач");
        Assertions.assertEquals(expectedTask, expectedTasks.get(0), "Задачи не совпадают");
    }

    @Test
    void addEpicTask() {
        Epic expectedEpic = taskManager.getEpicTasksById(2);

        Assertions.assertNotNull(expectedEpic, "Эпик не найден");
        Assertions.assertEquals(expectedEpic, epic, "Эпики не совпадают");

        final ArrayList<Epic> expectedEpics = taskManager.getEpicTasks();

        assertNotNull(expectedEpics, "Список эпиков не возвращается");
        assertEquals(1, expectedEpics.size(), "Неверное количество эпиков в списке");
        Assertions.assertEquals(expectedEpic, expectedEpics.get(0), "Эпики не совпадают");
    }

    @Test
    void addSubTask() {
        Subtask expectedSubtask = taskManager.getSubTasksById(3);

        Assertions.assertNotNull(expectedSubtask, "Подзадача не найдена");
        Assertions.assertEquals(expectedSubtask, subtask1, "Подзадачи не совпадают");

        final ArrayList<Subtask> expectedSubtasks = taskManager.getEpicSubTasks();

        assertNotNull(expectedSubtasks, "Список подзадач не возвращается");
        assertEquals(2, expectedSubtasks.size(), "Неверное количество подзадач в списке");
        Assertions.assertEquals(expectedSubtask, expectedSubtasks.get(0), "Подзадачи не совпадают");
    }
}