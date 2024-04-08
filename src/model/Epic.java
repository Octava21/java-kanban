package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
  private ArrayList<Integer> subTaskId;
  private LocalDateTime endTime;

  public Epic(String name, String description, String status) {
    super(name, description);
    this.subTaskId = new ArrayList<>();
  }

  public void addSubTaskId(int subTaskId) {
    this.subTaskId.add(subTaskId);
  }

  @Override
  public LocalDateTime getEndTime() {
    return endTime;
  }

  public ArrayList<Integer> getSubTaskId() {
    return subTaskId;
  }

  public void setSubTaskId(ArrayList<Integer> subTaskId) {
    this.subTaskId = subTaskId;
  }

  @Override
  public TaskType getType() {
    return TaskType.EPIC;
  }
}