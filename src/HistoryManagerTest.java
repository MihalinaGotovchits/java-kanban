import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    protected HistoryManager historyManager = new InMemoryHistoryManager();
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void init() {
        task = new Task("Задача", "Новая задача");
        epic = new Epic("Эпик", "Новый эпик");
        subtask = new Subtask("Подзадача", "Новая подзадача", 2);

    }

    @Test
    void addTask() {
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        assertEquals(0, task.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void addEpic() {
        historyManager.add(epic);
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        assertEquals(0, epic.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void addSubtask() {
        historyManager.add(subtask);
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        assertEquals(0, subtask.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void getHistory() {
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История отсутствует");
        assertTrue(history.isEmpty(), "История не пуста");

        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не сохранена");

        historyManager.add(task);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(3, history.size(), "История сохранена неверно");
        assertEquals(0, task.getId(), "История сохранена неверно");
    }
}