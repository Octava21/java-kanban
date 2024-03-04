package model;

import java.util.ArrayList;

public class Epic extends Task {
  private ArrayList<Integer> subTaskId;

  public Epic(String name, String description, String status) {
    super(name, description);
    this.subTaskId = new ArrayList<>();
  }

  public void addSubTaskId(int subTaskId) {
    this.subTaskId.add(subTaskId);
  }

  public ArrayList<Integer> getSubTaskId() {
    return subTaskId;
  }

  public void setSubTaskId(ArrayList<Integer> subTaskId) {
    this.subTaskId = subTaskId;
  }
}