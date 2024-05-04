import java.util.ArrayList;

public interface TaskManager {
    void addSimpleTask(Task task);

    void addEpicTask(Epic epic);

    void addSubTask(Subtask subtask);

    ArrayList<Task> getSimpleTask();

    ArrayList<Epic> getEpicTasks();

    ArrayList<Subtask> getEpicSubTasks();

    Task getTasksById(int taskId);

    Epic getEpicTasksById(int id);

    Subtask getSubTasksById(int id);

    void removeById(int id);

    void removeTasksLists(int command);

    ArrayList<Subtask> returnSubTaskListByOneEpic(int id);

    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSubtask(Subtask updateSubtask);

    void checkEpicStatus(int id);

    ArrayList<Task> getHistory();
}
