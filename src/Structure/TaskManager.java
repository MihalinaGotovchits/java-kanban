package Structure;

import java.util.ArrayList;

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

    ArrayList<Subtask> returnSubTaskListByOneEpic(int id);

    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSubtask(Subtask updateSubtask);

    ArrayList<Task> getHistory();
}
