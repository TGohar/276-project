package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.tatu.CircularProgressBar;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.ui.renderers.ComponentRenderer;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

// View for the project workspace
@Route("project")
@RouteAlias("project-workspace")
@PreserveOnRefresh
@PageTitle("Project Workspace | Trackour")
@PermitAll
public class ProjectView extends VerticalLayout implements HasUrlParameter<Integer> {

    @Autowired
    SecurityViewService securityViewService;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;

  // Create some UI components to display the project details
  private H1 title;
  private Div collaborationMode;
  private Div audioDetails;
  private Div status;


  // Constructor for the view class
  public ProjectView(
      SecurityViewService securityViewService,
      CustomUserDetailsService customUserDetailsService
      ) {
          setSizeFull();
          getStyle().setMargin("0%");
          getStyle().setPadding("0%");
          this.securityViewService = securityViewService;
          this.customUserDetailsService = customUserDetailsService;

          // CircularProgressBar progress = new CircularProgressBar();
          // progress.setWidth("200px");
          // progress.setHeight("200px");
          // progress.setPercent(0.5);
          // progress.setCaption("Loading...");
        }

  // Override the setParameter method to handle the route parameter
  @Override
  public void setParameter(BeforeEvent event, Integer id) {
      
      // Initialize the UI components
      title = new H1();
      collaborationMode = new Div();
      audioDetails = new Div();
      status = new Div();
      
      // Get the project object by id from some data source
      Project project = getProjectById(id);
      
      // Update the UI components with the project details
      title.setText(project.getName());
      collaborationMode.setText(project.getCollaborationMode());
      audioDetails.setText(project.getAudioDetails());
      status.setText(project.getStatus());
      
      Grid<Task> taskGrid = new Grid<>(Task.class);
      taskGrid.setSizeFull();
      taskGrid.setColumns("id", "name", "description", "assignee", "completed");
      taskGrid.setSelectionMode(Grid.SelectionMode.MULTI);
      
      // -----------------------------------------------------------------------------------------------------
      // Create an ArrayList of Task objects
      ArrayList<Task> tasks = new ArrayList<>();
      
      // Loop to create 10 random tasks and add them to the ArrayList
      for (int i = 0; i < 10; i++) {
          // Add the task to the ArrayList
          tasks.add(generateRandomTask(i, project));
      }
      // Populate the grid with the ArrayList of tasks
      taskGrid.setItems(tasks);
      // -----------------------------------------------------------------------------------------------------
      
      SplitLayout projectDetailsContainer = new SplitLayout();
      projectDetailsContainer.setSizeFull();
      // projectDetailsContainer.getStyle().setBackground("orange");
      // (top, bottom)
      projectDetailsContainer.setOrientation(SplitLayout.Orientation.HORIZONTAL);
      
      // main container contining cards area and button
      HorizontalLayout detailsSection = new HorizontalLayout();
      detailsSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
      detailsSection.setSizeFull();
      
      VerticalLayout participantsSection = new VerticalLayout();
      participantsSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
      participantsSection.setSizeFull();
      participantsSection.getStyle().setBackground("purple");

      // display assignee listbox for selected task
      MultiSelectListBox<User> listBox = new MultiSelectListBox<>();
      listBox.setItems(project.getParticipants());
      listBox.select(project.getFirstParticipant());
      
      VerticalLayout dataSection = new VerticalLayout();
      dataSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
      dataSection.setSizeFull();
      CircularProgressBar progressBar = new CircularProgressBar();
      progressBar.setCaption("Complete");
      progressBar.setWidth("12.5rem");
      progressBar.setHeight("12.5rem");
      progressBar.setPercent(project.getProgress());
      dataSection.add(progressBar);

      Span status_collab = new Span();
      status_collab.add(status, collaborationMode);

      Span songs_ausioDetails = new Span();
      dataSection.add(title, status_collab, audioDetails);
      
      detailsSection.add(dataSection, participantsSection);
      // detailsSection.getStyle().setBackground("blue");

      // main container contining cards area and button
      VerticalLayout editingSection = new VerticalLayout();
      editingSection.setSizeFull();
      TextArea taskDescriptionArea = new TextArea();
      taskDescriptionArea.setSizeFull();
      taskDescriptionArea.setWidthFull();
      taskDescriptionArea.setLabel("Description");
      taskDescriptionArea.setValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\r\n" + //
          "\r\n" + //
          "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat pulvinar nisl sed pellentesque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\r\n" + //
              "\r\n" + //
              "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat pulvinar nisl sed pellentesque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\r\n" + //
                  "\r\n" + //
                  "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat pulvinar nisl sed pellentesque.");
      editingSection.add(taskDescriptionArea);
      
      // editingSection.getStyle().setBackground("purple");

      // project details and editing section 
      projectDetailsContainer.addToPrimary(detailsSection);
      projectDetailsContainer.addToSecondary(editingSection);
      // projectDetailsContainer.setSplitterPosition(30);
      projectDetailsContainer.setOrientation(SplitLayout.Orientation.VERTICAL);
      
      // Create a new SplitLayout object
      SplitLayout splitLayout = new SplitLayout();

      // Set the orientation to horizontal (left, right)
      splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);

      // Add the verticalLayout and the taskGrid as its children
      splitLayout.addToPrimary(projectDetailsContainer);
      splitLayout.addToSecondary(taskGrid);

      // Set the split position to 50%
      splitLayout.setSplitterPosition(50);
      
      // Create a responsive navbar component
      NavBar navbar = new NavBar(customUserDetailsService, securityViewService);
      
      navbar.setContent(splitLayout);
      
      // Add it to the view
      this.removeAll();
      add(navbar);
  }

  private Task generateRandomTask(int  i, Project project) {
      // Create a Random object
      Random random = new Random();

      // Create an array of possible assignee values
      String[] assignees = {"Alice", "Bob", "Charlie", "David", "Eve"};

      // Create an array of possible completed values
      boolean[] completed = {true, false};

      // Generate random values for the fields of the Task object
      int taskId = i + 1;
      String name = "Task " + taskId;
      String description = "This is a random task";
      String assignee = assignees[random.nextInt(assignees.length)]; // Pick a random assignee from the array
      boolean isCompleted = completed[random.nextInt(completed.length)]; // Pick a random completed value from the array

      // Create and return a new Task object with the random values
      return new Task(taskId, name, description, project, assignee, isCompleted);
  }

    // Define a method to get a project by id
public Project getProjectById(Integer id) {
    // Get the project from a mock data service
    return MockDataService.getInstance().getProjectById(id);
  }
  
}
