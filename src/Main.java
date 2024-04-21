public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

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
        System.out.println(taskManager.getSimpleTask());

        System.out.println(taskManager.getEpicSubTasks());

        Subtask subtask2 = new Subtask("Подзадача2", "Новая подзадача2", 3, Status.DONE, 2);
        Subtask subtask3 = new Subtask("Подзадача2", "Новая подзадача2", 4, Status.DONE, 2);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);
        System.out.println(taskManager.getEpicSubTasks());
        System.out.println(taskManager.getEpicTasks());

        epic.setId(1);
        task.setId(1);
        subtask.setId(1);
        subtask1.setId(1);

        taskManager.removeAllTasksLists();

        System.out.println(taskManager.epicTasks);
        System.out.println(taskManager.simpleTasks);

    }
}