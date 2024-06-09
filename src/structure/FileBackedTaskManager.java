package structure;

import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(Path path) {
        this.file = new File(String.valueOf(path));
    }

    //строка константа. Будет записана первой в файл(обозначает название "столбцов")
    private static final String FIRST_LINE = "id,type,name,status,description,epic";

    //метод читает и восстанавливает задачи, которые были записаны в файл во время предыдущей работы
    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Path.of("./resources/kanban.csv"));
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> readTaskLine = reader.lines().toList();
            for (int i = 0; i < readTaskLine.size(); i++) {
                String[] line = readTaskLine.get(i).split(",");
                Task task = fromString(line[i]);
                switch (task.getTaskType()) {
                    case TASK:
                        fileManager.simpleTasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileManager.epicTasks.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subTasks.put(task.getId(), (Subtask) task);
                        int epicId = ((Subtask) task).getEpicId();
                        List<Integer> subtasksId = fileManager.epicTasks.get(epicId).getEpicsSubtasksId();
                        subtasksId.add(task.getId());
                        fileManager.recalculateEpicStatus(epicId);
                        break;
                }
                if (task.getId() > fileManager.idGenerator) {
                    fileManager.idGenerator = task.getId();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileManager;
    }

    //метод для создания задачи из строки
    private static Task fromString(String value) {
        String[] line = value.split(",");
        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String name = line[2];
        Status status = Status.valueOf(line[3]);
        String description = line[4];
        switch (taskType) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status);
            case SUBTASK:
                int epicId = Integer.parseInt(line[5]);
                return new Subtask(name, description, id, status, epicId);
        }
        return null;
    }

    //после каждой модифицирующей операции сохраняет задачи в файл
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(FIRST_LINE);
            writer.newLine();
            for (Task task : getSimpleTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpicTasks()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getEpicSubTasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    //метод создания строки для задачи
    private String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription();
    }

    //метод создания строки для эпика
    private String toString(Epic epic) {
        return epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                epic.getDescription();
    }

    //метод создания строки для подзадачи
    private String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getName() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    @Override
    public void addSimpleTask(Task task) {
        super.addSimpleTask(task);
        save();
    }

    @Override
    public void addEpicTask(Epic epic) {
        super.addEpicTask(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public Task getTasksById(int taskId) {
        Task task = super.getTasksById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicTasksById(int id) {
        Epic epic = super.getEpicTasksById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTasksById(int id) {
        Subtask subtask = super.getSubTasksById(id);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeSimpleTasks() {
        super.removeSimpleTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        super.updateSubtask(updateSubtask);
        save();
    }

    @Override
    public void recalculateEpicStatus(int id) {
        super.recalculateEpicStatus(id);
        save();
    }

    public static void main(String[] args) {
        Task task = new Task("Task", "NewTask");
        Task task1 = new Task("Task1", "NewTask1");
        Epic epic = new Epic("Epic", "NewEpic");
        Subtask subtask = new Subtask("SubTask", "BewSubTask", 3);
        Subtask subtask1 = new Subtask("SubTask1", "NewSubTask", 3);

        FileBackedTaskManager fileManager = new FileBackedTaskManager(Path.of("./resources/kanban.csv"));

        fileManager.addSimpleTask(task);
        fileManager.addSimpleTask(task1);
        fileManager.addEpicTask(epic);
        fileManager.addSubTask(subtask);
        fileManager.addSubTask(subtask1);
    }
}
