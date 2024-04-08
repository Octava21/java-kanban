package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected int id;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    private LocalDateTime startTime;
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, String startTime, String duration) {
        this.name = name;
        this.description = description;
        if (startTime != null) {
            this.startTime = LocalDateTime.parse(startTime, dateTimeFormatter);
        }
        if (duration != null) {
            this.duration = Duration.ofMinutes(Long.parseLong(duration));
        }
        this.status = TaskStatus.NEW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';

    }

    public abstract TaskType getType();
}