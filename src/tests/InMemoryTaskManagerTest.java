package tests;

import Structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        final List<Task> expectedTasks = taskManager.getSimpleTasks();

        assertNotNull(expectedTasks, "Задачи не возвращаются");
        assertEquals(1, expectedTasks.size(), "Неверное количество задач");
        Assertions.assertEquals(expectedTask, expectedTasks.get(0), "Задачи не совпадают");
    }

    @Test
    void addEpicTask() {
        Epic expectedEpic = taskManager.getEpicTasksById(2);

        Assertions.assertNotNull(expectedEpic, "Эпик не найден");
        Assertions.assertEquals(expectedEpic, epic, "Эпики не совпадают");

        final List<Epic> expectedEpics = taskManager.getEpicTasks();

        assertNotNull(expectedEpics, "Список эпиков не возвращается");
        assertEquals(1, expectedEpics.size(), "Неверное количество эпиков в списке");
        Assertions.assertEquals(expectedEpic, expectedEpics.get(0), "Эпики не совпадают");
    }

    @Test
    void addSubTask() {
        Subtask expectedSubtask = taskManager.getSubTasksById(3);

        Assertions.assertNotNull(expectedSubtask, "Подзадача не найдена");
        Assertions.assertEquals(expectedSubtask, subtask1, "Подзадачи не совпадают");

        final List<Subtask> expectedSubtasks = taskManager.getEpicSubTasks();

        assertNotNull(expectedSubtasks, "Список подзадач не возвращается");
        assertEquals(2, expectedSubtasks.size(), "Неверное количество подзадач в списке");
        Assertions.assertEquals(expectedSubtask, expectedSubtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void getHistory() {
        taskManager.getEpicTasksById(2);
        taskManager.getSubTasksById(4);
        taskManager.getTasksById(1);
        taskManager.getSubTasksById(3);
        taskManager.getTasksById(1);
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size(), "Список истории сформирован неверно");
        assertEquals(2, history.get(0).getId(), "Задача 2 не добавлена в список истории");
        assertEquals(4, history.get(1).getId(), "Задача 4 не добавлена в список истории");
        assertEquals(3, history.get(2).getId(), "Задача 3 не добавлена в список истории");
        assertEquals(1, history.get(3).getId(), "Задача 1 не добавлена в список истории");
    }

    @Test
    void removeTaskById() {
        assertNotNull(taskManager.getSimpleTasks(), "Список задач не заполнен");
        assertEquals(1, taskManager.getSimpleTasks().size(), "Неверное количество задач.");
        taskManager.removeTaskById(1);
        assertNull(taskManager.getTasksById(1), "Задача не удалена");
    }

    @Test
    void removeSubtaskById() {
        assertNotNull(taskManager.getEpicSubTasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getEpicSubTasks().size(), "Неверное количество задач.");
        taskManager.removeSubtaskById(3);
        assertNull(taskManager.getSubTasksById(3), "Подзадача не удалена");
    }

    @Test
    void removeEpicById() {
        assertNotNull(taskManager.getEpicTasks(), "Список эпиков не заполнен");
        taskManager.removeEpicById(2);
        assertNull(taskManager.getEpicTasksById(2), "Эпик не удален");
    }
}