package structure;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

   public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
