package structure;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description) &&
                Objects.equals(id, subtask.id) &&
                Objects.equals(status, subtask.status) &&
                (epicId == subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, epicId);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "id эпика=" + epicId +
                ", название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id + '\'' +
                ", статус=" + status +
                ", время начала" + startTime + '\'' +
                "продолжительность" + duration +
                '}';
    }
}