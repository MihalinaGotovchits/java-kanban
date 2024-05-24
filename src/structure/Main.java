package structure;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик", "Новый эпик");
        Subtask subtask = new Subtask("Подзадача", "Новая подзадача", 1);
        Subtask subtask1 = new Subtask("Подзадача2", "Новая подзадача2", 1);
        Subtask subtask2 = new Subtask("Подзадача 3", "Новая подзадача 3", 1);

        taskManager.addEpicTask(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        System.out.println(taskManager.getSubTasksById(4));
        System.out.println(taskManager.getSubTasksById(2));
        System.out.println(taskManager.getSubTasksById(3));
        System.out.println(taskManager.getSubTasksById(2));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getSubTasksById(4));
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getSubTasksById(3));
        System.out.println(taskManager.getHistory());
        taskManager.removeSubtaskById(2);
        taskManager.removeEpicById(1);
        System.out.println(taskManager.getHistory());
    }
}