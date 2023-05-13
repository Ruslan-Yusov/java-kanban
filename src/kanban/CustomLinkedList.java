package kanban;

import kanban.tasks.Task;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CustomLinkedList {
    private Node first;
    private Node last;
    private int size = 0;
    private final Map<Integer, Node> strangeMap = new HashMap<>();

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Node current = first; current != null; current = current.next) {
            tasks.add(current.item);
        }
        return tasks;
    }

    public void link(Task task) {
        if (task == null) {
            return;
        }
        if (size == 0) {
            first = new Node(null, task, null);
            last = first;
            size++;
            strangeMap.put(task.getId(), first);
        } else {
            Node found = strangeMap.get(task.getId());
            boolean wasFound = found != null;
            if (wasFound && found != last) {
                removeById(task.getId());
                linkLast(task);
            } else if (!wasFound) {
                linkLast(task);
                size++;
            }
        }
    }

    private void linkLast(Task task) {
        Node l = last;
        Node node = new Node(l, task, null);
        l.next = node;
        last = node;
        strangeMap.put(task.getId(), node);
    }

    public void removeById(int id) {
        Node current = strangeMap.get(id);
        if (current != null && current.item != null) {
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
            strangeMap.remove(id);
        }
    }

    private static class Node {
        private final Task item;
        private Node next;
        private Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
