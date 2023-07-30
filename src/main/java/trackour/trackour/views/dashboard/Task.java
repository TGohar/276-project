package trackour.trackour.views.dashboard;

public class Task {

  // Fields for each task
  private int id;
  private String name;
  private String description;
  private Project project; // The project that this task belongs to
  private String assignee; // The name of the person who is assigned to this task
  private boolean completed; // The status of this task

  // Constructor for each task
  public Task(int id, String name, String description, Project project, String assignee, boolean completed) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.project = project;
    this.assignee = assignee;
    this.completed = completed;
  }

  // Getters and setters for each field
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
