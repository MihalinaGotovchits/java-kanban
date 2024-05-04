import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public int idGenerator = 0;
    protected final HashMap<Integer, Task> simpleTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected final HistoryManager historyList = Managers.getDefaultHistory();

    @Override
    public void addSimpleTask(Task task) {
        int taskId = ++idGenerator;
        task.setId(taskId);
        simpleTasks.put(taskId, task);
    }

    @Override
    public void addEpicTask(Epic epic) {
        int epicId = ++idGenerator;
        epic.setId(epicId);
        epicTasks.put(epicId, epic);
    }

    @Override
    public void addSubTask(Subtask subtask) {
        int subTaskId = ++idGenerator;
        subtask.setId(subTaskId);
        subTasks.put(subTaskId, subtask);
        int epicId = subtask.getEpicId();
        ArrayList<Integer> subTasksId = epicTasks.get(epicId).getEpicsSubtasksId();
        subTasksId.add(subTaskId);
        checkEpicStatus(epicId);
    }

    @Override
    public ArrayList<Task> getSimpleTask() {
        if (simpleTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(simpleTasks.values());
        }
    }

    @Override
    public ArrayList<Epic> getEpicTasks() {
        if (epicTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(epicTasks.values());
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubTasks() {
        if (subTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(subTasks.values());
        }
    }

    //получение задачи по индентификатору из списка простых задач
    @Override
    public Task getTasksById(int taskId) {
        Task task = simpleTasks.get(taskId);
        if (taskId == 0 || simpleTasks.isEmpty()) {
            return null;
        } else {
            if (simpleTasks.containsKey(taskId)) {
                //вывести задачу
                task = simpleTasks.get(taskId);
                historyList.add(task);
            }
        }
        return task;
    }

    @Override
    public Epic getEpicTasksById(int id) {
        Epic epic = epicTasks.get(id);
        if (id == 0 || simpleTasks.isEmpty()) {
            return null;
        } else {
            if (epicTasks.containsKey(id)) {
                epic = epicTasks.get(id);
                historyList.add(epic);
            }
        }
        return epic;
    }

    @Override
    public Subtask getSubTasksById(int id) {
        Subtask subtask = subTasks.get(id);
        if (id == 0 || subTasks.isEmpty()) {
            return null;
        } else {
            if (subTasks.containsKey(id)) {
                subtask = subTasks.get(id);
                historyList.add(subtask);
            }
        }
        return subtask;
    }

    @Override
    public void removeById(int id) {
        if (id != 0) {
            if (simpleTasks.containsKey(id)) {
                simpleTasks.remove(id);
            } else if (epicTasks.containsKey(id)) {
                epicTasks.remove(id);
            } else if (subTasks.containsKey(id)) {
                subTasks.remove(id);
            }
        }
    }

    @Override
    public void removeTasksLists(int command) {
        switch (command) {
            case 1:
                simpleTasks.clear();
                break;
            case 2:
                epicTasks.clear();
                break;
            case 3:
                subTasks.clear();
                break;
        }
    }

    @Override
    public ArrayList<Subtask> returnSubTaskListByOneEpic(int id) {
        ArrayList<Integer> subTasksIds = epicTasks.get(id).getEpicsSubtasksId();
        ArrayList<Subtask> subTasksList = new ArrayList<>();
        for (int subTaskId : subTasksIds) {
            subTasksList.add(subTasks.get(subTaskId));
        }
        return subTasksList;
    }

    @Override
    public void updateTask(Task updateTask) {
        simpleTasks.put(updateTask.getId(), updateTask);
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        epicTasks.put(updateEpic.getId(), updateEpic);
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        int id = updateSubtask.getId();
        subTasks.put(id, updateSubtask);
        int epicId = epicTasks.get(id).getId();
        checkEpicStatus(epicId);
    }

    @Override
    public void checkEpicStatus(int id) {
        int counterNEW = 0;
        int counterDONE = 0;
        ArrayList<Integer> subTaskId = epicTasks.get(id).getEpicsSubtasksId();
        for (Integer subtaskId : subTaskId) {
            if (subTasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                counterNEW++;
            } else if (subTasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                counterDONE++;
            }
        }
        if (subTaskId.size() == counterNEW || subTaskId.isEmpty()) {
            epicTasks.get(id).setStatus(Status.NEW);
        } else if (subTasks.size() == counterDONE) {
            epicTasks.get(id).setStatus(Status.DONE);
        } else {
            epicTasks.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    //реализовать метод
    @Override
    public ArrayList<Task> getHistory() {
        return historyList.getHistory();
    }
}
