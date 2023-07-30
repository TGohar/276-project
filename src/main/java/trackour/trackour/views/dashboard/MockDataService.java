package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.List;

public class MockDataService {

  // A singleton instance of the mock data service
  private static MockDataService instance;

  // A list of sample projects
  private List<Project> projects;

  // A private constructor to prevent direct instantiation
  private MockDataService() {
    // Initialize the list of sample projects
    projects = new ArrayList<>();
    List<Task> tasks = new ArrayList<>();
    // Add some sample projects to the list
    projects.add(new Project(0, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.50, tasks));
    projects.add(new Project(1, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "In Progress", 0.75, tasks));
    projects.add(new Project(2, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "Completed", 1.0, tasks));
    projects.add(new Project(3, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.5, tasks));
    projects.add(new Project(4, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "Completed", 1.0, tasks));
    projects.add(new Project(5, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "In Progress", 0.25, tasks));
    projects.add(new Project(0, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.50, tasks));
    projects.add(new Project(1, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "In Progress", 0.75, tasks));
    projects.add(new Project(2, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "Completed", 1.0, tasks));
    projects.add(new Project(3, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.5, tasks));
    projects.add(new Project(4, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "Completed", 1.0, tasks));
    projects.add(new Project(5, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "In Progress", 0.25, tasks));
    projects.add(new Project(0, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.50, tasks));
    projects.add(new Project(1, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "In Progress", 0.75, tasks));
    projects.add(new Project(2, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "Completed", 1.0, tasks));
    projects.add(new Project(3, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.5, tasks));
    projects.add(new Project(4, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "Completed", 1.0, tasks));
    projects.add(new Project(5, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "In Progress", 0.25, tasks));
    projects.add(new Project(0, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.50, tasks));
    projects.add(new Project(1, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "In Progress", 0.75, tasks));
    projects.add(new Project(2, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "Completed", 1.0, tasks));
    projects.add(new Project(3, "Project A", "Solo", "Song: Hello, Key: C, BPM: 120", "In Progress", 0.5, tasks));
    projects.add(new Project(4, "Project B", "Team", "Song: Bye, Key: G, BPM: 100", "Completed", 1.0, tasks));
    projects.add(new Project(5, "Project C", "Duet", "Song: Hi, Key: D, BPM: 90", "In Progress", 0.25, tasks));
  }

  // Define a method to get a list of projects
  public List<Project> getProjects() {
    // Return a copy of the list of sample projects
    return new ArrayList<>(projects);
    }  

  // A public method to get the singleton instance of the mock data service
  public static MockDataService getInstance() {
    // If the instance is null, create a new one
    if (instance == null) {
      instance = new MockDataService();
    }
    // Return the instance
    return instance;
  }

  // A public method to get a project by id
  public Project getProjectById(Integer id) {
    // Loop through the list of projects
    for (Project project : projects) {
      // If the project id matches the parameter, return the project
      if (project.getId() == id) {
        return project;
      }
    }
    // If no project is found, return null
    return null;
  }
}
