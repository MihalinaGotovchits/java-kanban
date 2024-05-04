import java.util.ArrayList;

public class Managers {
    public static TaskManager getDefault() {
        return new TaskManager() {
            @Override
            public void addSimpleTask(Task task) {

            }

            @Override
            public void addEpicTask(Epic epic) {

            }

            @Override
            public void addSubTask(Subtask subtask) {

            }

            @Override
            public ArrayList<Task> getSimpleTask() {
                return null;
            }

            @Override
            public ArrayList<Epic> getEpicTasks() {
                return null;
            }

            @Override
            public ArrayList<Subtask> getEpicSubTasks() {
                return null;
            }

            @Override
            public Task getTasksById(int taskId) {
                return null;
            }

            @Override
            public Epic getEpicTasksById(int id) {
                return null;
            }

            @Override
            public Subtask getSubTasksById(int id) {
                return null;
            }

            @Override
            public void removeById(int id) {

            }

            @Override
            public void removeTasksLists(int command) {

            }

            @Override
            public ArrayList<Subtask> returnSubTaskListByOneEpic(int id) {
                return null;
            }

            @Override
            public void updateTask(Task updateTask) {

            }

            @Override
            public void updateEpic(Epic updateEpic) {

            }

            @Override
            public void updateSubtask(Subtask updateSubtask) {

            }

            @Override
            public void checkEpicStatus(int id) {

            }

            @Override
            public ArrayList<Task> getHistory() {
                return null;
            }
        };
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
