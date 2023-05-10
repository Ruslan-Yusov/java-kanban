package kanban;

import kanban.tasks.Task;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomLinkedList {
    private Node first;
    private Node last;
    private int size = 0;

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Node current = first; current != null; current = current.next) {
            tasks.add(current.item);
        }
        return tasks;
    }

    public void link(Task task) {
        if (size == 0) {
            first = new Node(null, task, null);
            last = first;
            size++;
        } else {
            boolean wasFound = false;
            for (Node current = first; current != null; current = current.next) {
                wasFound = current.item == task;
                if (wasFound && current != last) {
                    if (current != first) {
                        Node p = current.prev;
                        p.next = current.next;
                    }
                    Node n = current.next;
                    n.prev = current.prev;
                    current.prev = last;
                    current.next = null;
                    last.next = current;
                    last = current;
                }
            }
            if (!wasFound) {
                Node l = last;
                Node node = new Node(l, task, null);
                l.next = node;
                last = node;
                size++;
            }
        }
    }

    public void removeById(int id) {
        for (Node current = first; current != null; current = current.next) {
            if (current.item != null && current.item.getId() == id) {
                if (current != first) {
                    Node p = current.prev;
                    p.next = current.next;
                } else {
                    first = first.next;
                }
                if (current != last) {
                    Node n = current.next;
                    n.prev = current.prev;
                } else {
                    last = last.prev;
                }
                size--;
            }
        }
    }

    public void removeFirst() {
        if (first != null) {
            Node f = first.next;
            first.next = null;
            first = f;
        }
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}