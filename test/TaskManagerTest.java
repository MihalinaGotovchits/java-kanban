import exceptions.CollectionTaskException;
import structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected InMemoryTaskManager taskManager = new InMemoryTaskManager();
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected final LocalDateTime DATE = LocalDateTime.of(2024, Month.JUNE, 21, 11, 11);

    @BeforeEach
    void initTasks() {
        task = new Task("Задача", "Новая задача", Duration.ofMinutes(40), DATE);
        taskManager.addSimpleTask(task);
        epic = new Epic("Эпик", "Новый эпик");
        taskManager.addEpicTask(epic);
        subtask1 = new Subtask("Подзадача1", "Новая подзадача 1", 2, Duration.ofMinutes(1),
                DATE.plusDays(1));
        taskManager.addSubTask(subtask1);
        subtask2 = new Subtask("Подзадача2", "Новая подзадача 2", 2, Duration.ofMinutes(1),
                DATE.plusDays(2));
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
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(taskPriority, expectedTask, "В список приоритизации добавлена неверная задача");
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
        Epic expectedEpicOfSubtask = taskManager.getEpicTasksById(2);
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не null");
        Subtask expectedSubtask = taskManager.getSubTasksById(3);

        Assertions.assertNotNull(expectedSubtask, "Подзадача не найдена");
        Assertions.assertEquals(expectedSubtask, subtask1, "Подзадачи не совпадают");

        final List<Subtask> expectedSubtasks = taskManager.getEpicSubTasks();

        assertNotNull(expectedSubtasks, "Список подзадач не возвращается");
        assertEquals(2, expectedSubtasks.size(), "Неверное количество подзадач в списке");
        Assertions.assertEquals(expectedSubtask, expectedSubtasks.get(0), "Подзадачи не совпадают");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(subtaskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(subtaskPriority, expectedSubtask, "В список приоритизации добавлена неверная задача");
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void checkEpicStatus() {
        Epic expectedEpicOfSubtask = taskManager.getEpicTasksById(2);
        Subtask updateSubtask4 = new Subtask("Подзадача", "description4", 4, Status.DONE, 2,
                Duration.ofMinutes(1), DATE.plusDays(2));
        taskManager.updateSubtask(updateSubtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        Subtask updateSubtask3 = new Subtask("Подзадача", "description3", 3, Status.DONE, 2,
                Duration.ofMinutes(1), DATE.plusDays(1));
        Subtask update2Subtask4 = new Subtask("Подзадача", "description4", 4, Status.DONE, 2,
                Duration.ofMinutes(1), DATE.plusDays(2));
        taskManager.updateSubtask(updateSubtask3);
        taskManager.updateSubtask(update2Subtask4);
        assertEquals(Status.DONE, expectedEpicOfSubtask.getStatus(), "Статус не DONE");
        Subtask update2Subtask3 = new Subtask("Подзадача", "description3", 3, Status.IN_PROGRESS,
                2, Duration.ofMinutes(2), DATE.plusDays(1));
        Subtask update3Subtask4 = new Subtask("Подзадача", "description4", 4, Status.IN_PROGRESS,
                2, Duration.ofMinutes(2), DATE.plusDays(2));
        taskManager.updateSubtask(update2Subtask3);
        taskManager.updateSubtask(update3Subtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        assertEquals(Duration.parse("PT4M"), expectedEpicOfSubtask.getDuration(),
                "Продолжительность эпика не обновилась");
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
    void getPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.get(0).getId(), "Задача 1 не приоритизирована");
        assertEquals(3, prioritizedTasks.get(1).getId(), "Задача 3 не приоритизирована");
        assertEquals(4, prioritizedTasks.get(2).getId(), "Задача 4 не приоритизирована");
    }

    @Test
    void removeTaskById() {
        assertNotNull(taskManager.getSimpleTasks(), "Список задач не заполнен");
        assertEquals(1, taskManager.getSimpleTasks().size(), "Неверное количество задач.");
        taskManager.removeTaskById(1);
        assertNull(taskManager.getTasksById(1), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNull(taskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void removeSubtaskById() {
        assertNotNull(taskManager.getEpicSubTasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getEpicSubTasks().size(), "Неверное количество задач.");
        taskManager.removeSubtaskById(3);
        assertNull(taskManager.getSubTasksById(3), "Подзадача не удалена");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNull(subtaskPriority, "Задача не удалена из списка приоритизации");
        assertEquals(DATE.plusDays(2), taskManager.getEpicTasksById(2).getStartTime(),
                "Время эпика не изменилось");
    }

    @Test
    void removeEpicById() {
        assertNotNull(taskManager.getEpicTasks(), "Список эпиков не заполнен");
        taskManager.removeEpicById(2);
        assertNull(taskManager.getEpicTasksById(2), "Эпик не удален");
    }

    @Test
    void validate() {
        Task task1 = new Task("Задача1", "description1", Duration.ofMinutes(1), DATE);
        Task task2 = new Task("Задача2", "description2", Duration.ofMinutes(1), DATE);

        CollectionTaskException exception = assertThrows(CollectionTaskException.class,
                () -> {
                    taskManager.addSimpleTask(task1);
                    taskManager.addSimpleTask(task2);
                });
        assertEquals("Время выполнения переданной задачи пересекается с временем выполнения "
                + "уже существующей задачи", exception.getMessage());
    }
}
