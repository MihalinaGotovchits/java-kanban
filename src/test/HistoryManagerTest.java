package test;

import structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    protected HistoryManager historyManager = new InMemoryHistoryManager();
    Task task;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void init() {
        task = new Task("Задача", "Новая задача", 1, Status.NEW);
        epic = new Epic("Эпик", "Новый эпик", 2, Status.NEW);
        subtask = new Subtask("Подзадача", "Новая подзадача", 3, Status.NEW, 2);
        subtask2 = new Subtask("Подзадача 2", "Новая подзадача 2", 4, Status.NEW, 2);
    }

    @Test
    void addTask() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        Assertions.assertEquals(1, task.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void addEpic() {
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        assertEquals(2, epic.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void addSubtask() {
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не найдена");
        assertEquals(1, history.size(), "История пуста");
        Assertions.assertEquals(3, subtask.getId(), "Иcтория сохранена с ошибкой");
    }

    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История отсутствует");
        assertTrue(history.isEmpty(), "История не пуста");

        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не сохранена");

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая");

        historyManager.add(task);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(1, task.getId(), "История сохранена неверно");
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertEquals(3, history.size(), "История сохранена неверно");
        //удаление из начала истории
        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
        assertEquals(3, history.get(1).getId(), "История сохранена неверно");
        //удаление из середины истории
        historyManager.add(subtask2);
        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
        assertEquals(4, history.get(1).getId(), "История сохранена неверно");
        //удаление с конца истории
        historyManager.remove(4);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
    }
}