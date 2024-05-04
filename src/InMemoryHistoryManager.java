import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> historyList = new ArrayList<>();
    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
            if (historyList.size() < 10) {
                historyList.add(task);
            }
            else {
                historyList.remove(0);
                historyList.add(task);
            }
        }
    }

