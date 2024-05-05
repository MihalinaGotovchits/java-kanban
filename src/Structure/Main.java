package Structure;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача", "Новая задача");
        Epic epic = new Epic("Эпик", "Новый эпик");
        Subtask subtask = new Subtask("Подзадача", "Новая подзадача", 2);
        Subtask subtask1 = new Subtask("Подзадача2", "Новая подзадача2", 2);

        taskManager.addSimpleTask(task);
        taskManager.addEpicTask(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask1);

        Task task1 = new Task("Задача1", "Новая задача1");
        taskManager.updateTask(task1);
        System.out.println(taskManager.getSimpleTasks());

    }
}