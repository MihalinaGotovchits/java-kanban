package structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final QueueLinkedList historyList = new QueueLinkedList();

    //метод для получения списка просмотренных задач
    @Override
    public ArrayList<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void add(Task task) {
        historyList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(id);
    }

    //класс для создания двусвязного списка
    private static class QueueLinkedList {

        //класс для создания узла в коллекции
        private static class Node<T> {
            public T task;
            public Node<T> next;
            public Node<T> prev;

            public Node(Node<T> prev, T task, Node<T> next) {
                this.task = task;
                this.next = next;
                this.prev = prev;
            }
        }

        private Node<Task> head;
        private Node<Task> tail;

        //мапа для хранения пары id задачи - узел
        final Map<Integer, QueueLinkedList.Node<Task>> idNode = new HashMap<>();


        //метод добавляет задачу в конец списка и удаляет ее предыдущий просмотр
        public void linkLast(Task task) {
            if (idNode.containsKey(task.getId())) {
                removeNode(idNode.get(task.getId()));
            }
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            idNode.put(task.getId(), newNode);
        }

        //метод принимает id задачи на удаление и передает его на удаление в метод удаления узла
        private void removeNode(int id) {
            if (idNode.containsKey(id)) {
                removeNode(idNode.get(id));
            }
        }

        //метод удаляет из мапы узел с задачей, которая уже была просмотрена ранее
        private void removeNode(Node<Task> node) {
            final Node<Task> prev = node.prev;
            final Node<Task> next = node.next;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
        }

        //метод для просмотра истории
        private ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            for (Node<Task> node = head; node != null; node = node.next) {
                tasks.add(node.task);
            }
            return tasks;
        }
    }
}

