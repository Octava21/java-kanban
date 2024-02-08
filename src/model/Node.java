package model;

public class Node {
    private Node prev;
    private Node next;
    private Task task;

    public Node(Task task) {
        this.task = task;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public Task getTask() {
        return task;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
