package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (taskMap.containsKey(task.getId())) {
            removeNode(taskMap.get(task.getId()));
        }
        taskMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            removeNode(taskMap.get(id));
            taskMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
                tasks.add(current.data);
                current = current.next;
            }
            return tasks;
        }

    private Node<Task> linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            return newNode;
        }

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
            node.data = null;
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