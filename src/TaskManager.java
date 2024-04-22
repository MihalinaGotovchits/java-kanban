import java.util.*;

public class TaskManager {
    public int idGenerator = 0;
    protected final HashMap<Integer, Task> simpleTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    public void addSimpleTask(Task task) {
        int taskId = ++idGenerator;
        task.setId(taskId);
        simpleTasks.put(taskId, task);
    }

    public void addEpicTask(Epic epic) {
        int epicId = ++idGenerator;
        epic.setId(epicId);
        epicTasks.put(epicId, epic);
    }

    public void addSubTask(Subtask subtask) {
        int subTaskId = ++idGenerator;
        subtask.setId(subTaskId);
        subTasks.put(subTaskId, subtask);
        int epicId = subtask.getEpicId();
        ArrayList<Integer> subTasksId = epicTasks.get(epicId).getEpicsSubtasksId();
        subTasksId.add(subTaskId);
        checkEpicStatus(epicId);
    }

    public ArrayList<Task> getSimpleTask() {
        if (simpleTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(simpleTasks.values());
        }
    }

    public ArrayList<Epic> getEpicTasks() {
        if (epicTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(epicTasks.values());
        }
    }

    public ArrayList<Subtask> getSubTasks() {
        if (subTasks.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(subTasks.values());
        }
    }

    public Task getTaskInSimpleTasksById(int taskId) {
        Task task = simpleTasks.get(taskId);
        if (taskId == 0 || simpleTasks.isEmpty()) {
            return null;
        } else {
            if (simpleTasks.containsKey(taskId)) {
                task = simpleTasks.get(taskId);
            }
        }
        return task;
    }

    public Epic getTaskInEpicTasksById(int id) {
        Epic epic = epicTasks.get(id);
        if (id == 0 || simpleTasks.isEmpty()) {
            return null;
        } else {
            if (epicTasks.containsKey(id)) {
                epic = epicTasks.get(id);
            }
        }
        return epic;
    }

    public Subtask getTaskInSubTasksById(int id) {
        Subtask subtask = subTasks.get(id);
        if (id == 0 || subTasks.isEmpty()) {
            return null;
        } else {
            if (subTasks.containsKey(id)) {
                subtask = subTasks.get(id);
            }
        }
        return subtask;
    }

    public void removeTaskById(int id) {
        if (id != 0) {
            if (simpleTasks.containsKey(id)) {
                simpleTasks.remove(id);
            }
        }
    }

    public void removeEpicById(int id) {
        if (id != 0) {
            if (epicTasks.containsKey(id)) {
                ArrayList<Integer> subTaskId = epicTasks.get(id).getEpicsSubtasksId();
                for (Integer subtaskId : subTaskId) {
                    subTasks.remove(subtaskId);
                }
                epicTasks.remove(id);
            }
        }
    }

    public void removeSubtaskById(int id) {
        if (id != 0) {
            if (subTasks.containsKey(id)) {
                int epicId = subTasks.get(id).getEpicId();
                ArrayList<Integer> subtaskId = epicTasks.get(epicId).getEpicsSubtasksId();
                subtaskId.remove(id);
                subTasks.remove(id);
                checkEpicStatus(epicId);
            }
        }
    }

    public void removeAll() {
        simpleTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    public void removeSimpleTask() {
        simpleTasks.clear();
    }

    public void removeAllEpics() {
        for (Epic epic : epicTasks.values()) {
            ArrayList<Integer> subtaskIds = epicTasks.get(epic.getId()).getEpicsSubtasksId();
            for (Integer subtaskId : subtaskIds) {
                subTasks.remove(subtaskId);
            }
        }
        epicTasks.clear();
    }

    public void removeAllSubTasks() {
        for (Epic epic : epicTasks.values()) {
            epic.getEpicsSubtasksId().clear();
            checkEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    public ArrayList<Subtask> getAllSubtasksIdByEpicId(int id) {
        ArrayList<Integer> subTasksIds = epicTasks.get(id).getEpicsSubtasksId();
        ArrayList<Subtask> subTasksList = new ArrayList<>();
        for (int subTaskId : subTasksIds) {
            subTasksList.add(subTasks.get(subTaskId));
        }
        return subTasksList;
    }

    public void updateTask(Task updateTask) {
        simpleTasks.put(updateTask.getId(), updateTask);
    }

    public void updateEpic(Epic updateEpic) {
        epicTasks.put(updateEpic.getId(), updateEpic);
    }

    public void updateSubtask(Subtask updateSubtask) {
        subTasks.put(updateSubtask.getId(), updateSubtask);
        checkEpicStatus(updateSubtask.getEpicId());
    }

    private void checkEpicStatus(int id) {
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
}