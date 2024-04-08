package model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId , String startTime, String duration) {
        super(name, description , startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}