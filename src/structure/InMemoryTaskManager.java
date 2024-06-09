package structure;

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
        recalculateEpicStatus(epicId);
    }

    @Override
    public ArrayList<Task> getSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //получение задачи по индентификатору из списка простых задач
    @Override
    public Task getTasksById(int taskId) {
        Task task = simpleTasks.get(taskId);
        if (task != null) {
            historyList.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicTasksById(int id) {
        Epic epic = epicTasks.get(id);
        if (epic != null) {
            historyList.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubTasksById(int id) {
        Subtask subtask = subTasks.get(id);
        if (subtask != null) {
            historyList.add(subtask);
        }
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        simpleTasks.remove(id);
        historyList.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        ArrayList<Integer> subTaskId = epicTasks.get(id).getEpicsSubtasksId();
        for (Integer subtaskId : subTaskId) {
            subTasks.remove(subtaskId);
            historyList.remove(subtaskId);
        }
        epicTasks.remove(id);
        historyList.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        int epicId = subTasks.get(id).getEpicId();
        ArrayList<Integer> subtaskId = epicTasks.get(epicId).getEpicsSubtasksId();
        subtaskId.remove((Integer) id);
        subTasks.remove(id);
        historyList.remove(id);
        recalculateEpicStatus(epicId);
    }

    @Override
    public void removeSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epicTasks.values()) {
            ArrayList<Integer> subtaskIds = epicTasks.get(epic.getId()).getEpicsSubtasksId();
            for (Integer subtaskId : subtaskIds) {
                subTasks.remove(subtaskId);
            }
        }
        epicTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epicTasks.values()) {
            epic.getEpicsSubtasksId().clear();
            recalculateEpicStatus(epic.getId());
        }
        subTasks.clear();
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
        recalculateEpicStatus(epicId);
    }

    public void recalculateEpicStatus(int id) {
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

    @Override
    public List<Task> getHistory() {
        return historyList.getHistory();
    }
}
