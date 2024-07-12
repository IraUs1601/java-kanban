package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskMap = new HashMap<>();
    private final CustomLinkedList<Task> taskList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskList.removeNode(taskMap.get(task.getId()));
        }
        taskMap.put(task.getId(), taskList.linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            taskList.removeNode(taskMap.get(id));
            taskMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskList.getTasks();
    }

    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        private List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.data);
                current = current.next;
            }
            return tasks;
        }

        private Node<T> linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            return newNode;
        }

        private void removeNode(Node<T> node) {
            final Node<T> prev = node.prev;
            final Node<T> next = node.next;
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
            node.data = null;
        }
    }

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(Node<T> prev, T element, Node<T> next) {
            this.data = element;
            this.prev = prev;
            this.next = next;
        }
    }
}