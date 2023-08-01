package trackour.trackour.views.dashboard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import trackour.trackour.model.user.User;

public class Project {

    /**
     * 
     */

  // Fields for each project
  private int id;
  private String name;
  private String collaborationMode;
  private String audioDetails;
  private String status;
  private double progress;
  private List<TaskMock> tasks; // The list of tasks for this project
  private Set<User> participants; // The list of tasks for this project
  private User firstParticipant;

  // Constructor for each project
  public Project(int id, String name, String collaborationMode, String audioDetails, String status, double progress, List<TaskMock> tasks) {
    this.id = id;
    this.name = name;
    this.collaborationMode = collaborationMode;
    this.audioDetails = audioDetails;
    this.status = status;
    this.progress = progress;
    this.tasks = tasks;
    this.participants = new HashSet<>();
    this.firstParticipant = new User();
    firstParticipant.setUsername("user1");
    firstParticipant.setUid(50l);

    User participant1 = new User();
    participant1.setUsername("user2");
    participant1.setUid(52l);
    participants.add(firstParticipant);
    participants.add(participant1);
  }

  // Getters and setters for each field
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  // Getters and setters for each field
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCollaborationMode() {
    return collaborationMode;
  }

  public void setCollaborationMode(String collaborationMode) {
    this.collaborationMode = collaborationMode;
  }

  public String getAudioDetails() {
    return audioDetails;
  }

  public void setAudioDetails(String audioDetails) {
    this.audioDetails = audioDetails;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public double getProgress() {
    return progress;
  }

  public void setProgress(double progress) {
    this.progress = progress;
  }

  public List<TaskMock> getTasks() {
    return tasks;
  }

  public void setTasks(List<TaskMock> tasks) {
    this.tasks = tasks;
  }

  public Set<User> getParticipants() {
    return this.participants;
  }

  public void setParticipants(Set<User> participants) {
    this.participants = participants;
  }

  public User getFirstParticipant() {
    return firstParticipant;
  }
}

