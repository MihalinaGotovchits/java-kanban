package structure;

import exceptions.CollectionTaskException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    public int idGenerator = 0;
    protected final HashMap<Integer, Task> simpleTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected final HistoryManager historyList = Managers.getDefaultHistory();
    protected Comparator<Task> TaskComparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected Set<Task> prioritizedTasks = new TreeSet<>(TaskComparator);

    @Override
    public void addSimpleTask(Task task) {
        validate(task);
        int taskId = ++idGenerator;
        task.setId(taskId);
        simpleTasks.put(taskId, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpicTask(Epic epic) {
        int epicId = ++idGenerator;
        epic.setId(epicId);
        epicTasks.put(epicId, epic);
    }

    @Override
    public void addSubTask(Subtask subtask) {
        validate(subtask);
        int subTaskId = ++idGenerator;
        subtask.setId(subTaskId);
        subTasks.put(subTaskId, subtask);
        int epicId = subtask.getEpicId();
        ArrayList<Integer> subTasksId = epicTasks.get(epicId).getEpicsSubtasksId();
        subTasksId.add(subTaskId);
        recalculateEpicStatus(epicId);
        setEpicDateTime(epicId);
        prioritizedTasks.add(subtask);
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
        prioritizedTasks.remove(simpleTasks.get(id));
        simpleTasks.remove(id);
        historyList.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        ArrayList<Integer> subTaskId = epicTasks.get(id).getEpicsSubtasksId();
        for (Integer subtaskId : subTaskId) {
            prioritizedTasks.remove(subTasks.get(subtaskId));
            subTasks.remove(subtaskId);
            historyList.remove(subtaskId);
        }
        epicTasks.remove(id);
        historyList.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        prioritizedTasks.remove(subTasks.get(id));
        int epicId = subTasks.get(id).getEpicId();
        ArrayList<Integer> subtaskId = epicTasks.get(epicId).getEpicsSubtasksId();
        subtaskId.remove((Integer) id);
        subTasks.remove(id);
        historyList.remove(id);
        recalculateEpicStatus(epicId);
        setEpicDateTime(epicId);
    }

    @Override
    public void removeSimpleTasks() {
        for (Integer id : simpleTasks.keySet()) {
            historyList.remove(id);
            prioritizedTasks.remove(simpleTasks.get(id));
        }
        simpleTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        List<Integer> allSubtaskId = epicTasks.values().stream()
                .flatMap(epic -> epic.getEpicsSubtasksId().stream())
                .collect(Collectors.toList());

        prioritizedTasks.removeAll(allSubtaskId.stream()
                .map(subTasks::get)
                .collect(Collectors.toList()));

        allSubtaskId.forEach(historyList::remove);
        epicTasks.keySet().forEach(historyList::remove);
        epicTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        epicTasks.values().forEach(epic -> {
            epic.getEpicsSubtasksId().clear();
            recalculateEpicStatus(epic.getId());
            setEpicDateTime(epic.getId());
        });
        subTasks.values().stream()
                .map(Subtask::getEpicId)
                .forEach(historyList::remove);
        subTasks.values().forEach(prioritizedTasks::remove);
        subTasks.clear();
    }

    @Override
    public List<Subtask> returnSubTaskListByOneEpic(int id) {
        List<Integer> subTasksId = epicTasks.get(id).getEpicsSubtasksId();
        return subTasksId.stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(Task updateTask) {
        int id = updateTask.getId();
        validate(updateTask);
        prioritizedTasks.remove(simpleTasks.get(id));
        simpleTasks.put(updateTask.getId(), updateTask);
        prioritizedTasks.add(updateTask);
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        epicTasks.put(updateEpic.getId(), updateEpic);
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        int id = updateSubtask.getId();
        validate(updateSubtask);
        prioritizedTasks.remove(subTasks.get(id));
        subTasks.put(id, updateSubtask);
        int epicId = subTasks.get(id).getEpicId();
        recalculateEpicStatus(epicId);
        setEpicDateTime(epicId);
        prioritizedTasks.add(updateSubtask);
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

    @Override
    public void setEpicDateTime(int epicId) {
        ArrayList<Integer> subtasksId = epicTasks.get(epicId).getEpicsSubtasksId();
        if (subtasksId.isEmpty()) {
            epicTasks.get(epicId).setDuration(null);
            epicTasks.get(epicId).setStartTime(null);
            epicTasks.get(epicId).setEndTime(null);
            return;
        }
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        long epicDuration = 0L;
        for (Integer subtaskId : subtasksId) {
            Subtask subtask = subTasks.get(subtaskId);
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime != null) {
                if (epicStartTime == null || subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            if (subtaskEndTime != null) {
                if (epicEndTime == null || subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epicDuration = epicDuration + subTasks.get(subtaskId).getDuration().toMinutes();
        }
        epicTasks.get(epicId).setStartTime(epicStartTime);
        epicTasks.get(epicId).setEndTime(epicEndTime);
        epicTasks.get(epicId).setDuration(Duration.ofMinutes(epicDuration));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task task) {
        List<Task> prioritizedTask = getPrioritizedTasks();
        for (Task currentTask : prioritizedTask) {
            if (task.getStartTime() == null || currentTask.getStartTime() == null) {
                return;
            }
            if (Objects.equals(task.getId(), currentTask.getId())) {
                continue;
            }
            if ((!task.getEndTime().isAfter(currentTask.getStartTime())) ||
                    (!task.getStartTime().isBefore(currentTask.getEndTime()))) {
                continue;
            }
            throw new CollectionTaskException("Время выполнения переданной задачи пересекается с временем выполнения "
                    + "уже существующей задачи");
        }
    }
}
