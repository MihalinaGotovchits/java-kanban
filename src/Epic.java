import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private ArrayList<Integer> epicsSubtasksId = new ArrayList<>();

    public Epic(String name, String description, int id, Status status, ArrayList<Integer> epicsSubtasksId) {
        super(name, description, id, status);
        this.epicsSubtasksId = epicsSubtasksId;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public ArrayList<Integer> getEpicsSubtasksId(){
        return epicsSubtasksId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                Objects.equals(id, epic.id) &&
                Objects.equals(status, epic.status) &&
                Objects.equals(epicsSubtasksId, epic.epicsSubtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicsSubtasksId);
    }

    @Override
    public String toString() {
        if (epicsSubtasksId.isEmpty()){
            return "Epic{" +
                    ", название='" + name + '\'' +
                    ", описание='" + description + '\'' +
                    ", id=" + id +
                    ", статус=" + status +
                    '}';
        }
        else {
            return
                    "Epic{" +
                            ", название='" + name + '\'' +
                            ", описание='" + description + '\'' +
                            ", id=" + id +
                            ", статус=" + status +
                            ", id подзадачи='" + epicsSubtasksId + '}' + '\'';
        }
    }
}