package structure;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addSimpleTask(Task task);

    void addEpicTask(Epic epic);

    void addSubTask(Subtask subtask);

    ArrayList<Task> getSimpleTasks();

    ArrayList<Epic> getEpicTasks();

    ArrayList<Subtask> getEpicSubTasks();

    Task getTasksById(int taskId);

    Epic getEpicTasksById(int id);

    Subtask getSubTasksById(int id);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void removeSimpleTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    List<Subtask> returnSubTaskListByOneEpic(int id);

    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSubtask(Subtask updateSubtask);

    List<Task> getHistory();

    void setEpicDateTime(int epicId);

    List<Task> getPrioritizedTasks();

    void validate(Task task);
}
