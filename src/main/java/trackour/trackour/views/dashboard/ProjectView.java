package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.jhoffmann99.TrixEditor;
import org.vaadin.addons.tatu.CircularProgressBar;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.ui.renderers.ComponentRenderer;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.task.Task;

// View for the project workspace
@Route("project")
@RouteAlias("project-workspace")
@PreserveOnRefresh
@PageTitle("Project Workspace | Trackour")
@PermitAll
public class ProjectView extends VerticalLayout implements HasUrlParameter<String> {

    @Autowired
    SecurityViewService securityViewService;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    ProjectsService projectsService;

  // Create some UI components to display the project details
  private H1 title;
  private Span collaborationMode;
  private Div audioDetails;
  private Span status;


  // Constructor for the view class
  public ProjectView(
      SecurityViewService securityViewService,
      CustomUserDetailsService customUserDetailsService,
      ProjectsService projectsService
      ) {
          setSizeFull();
          getStyle().setMargin("0%");
          getStyle().setPadding("0%");
          this.securityViewService = securityViewService;
          this.customUserDetailsService = customUserDetailsService;
          this.projectsService = projectsService;
        }
        
        // Override the setParameter method to handle the route parameter
        @Override
        public void setParameter(BeforeEvent event, String id) {
            
            // Initialize the UI components
            title = new H1();
            collaborationMode = new Span();
            audioDetails = new Div();
            status = new Span();
      
      // Create a new SplitLayout object
      SplitLayout splitLayout = new SplitLayout();
      
      // Get the project object by id from some data source
      Optional<Project> projOptional = getProjectById(id);
      if (projOptional.isPresent()) {
          Grid<Task> taskGrid = new Grid<>(Task.class);
          
          Project project = projOptional.get();
          
          // Update the UI components with the project details
          title.setText(project.getTitle());
          // Create a new Span object and set its text to the project's collaboration mode value
          collaborationMode = new Span(project.getCollaborationMode().getValue());
          // Use a switch statement to add the badge theme variants to the span according to the collaboration mode value
          switch (project.getCollaborationMode()) {
              case SOLO:
                  collaborationMode.getElement().getThemeList().add("badge contrast small");
                  break;
              case TEAM:
                  collaborationMode.getElement().getThemeList().add("badge primary small");
                  break;
          }
          // map.put("keys", Arrays.asList("C#", "Bb"));
          // map.put("bpm", Arrays.asList("130", "90"));
          Div keys = new Div();
          keys.setText(project.getKeys().toString());
          Div bpm = new Div();
          bpm.add(project.getBpm().toString());
          status.setText(project.getStatus().getValue() + "");
          // Use a switch statement to add the badge theme variants to the span according to the status value
          switch (project.getStatus()) {
            case COMPLETED:
                status.getElement().getThemeList().add("badge success small");
                break;
            case IN_PROGRESS:
                status.getElement().getThemeList().add("badge primary small");
                break;
            }
          
          taskGrid.setSizeFull();
          taskGrid.setColumns("id", "title", "description", "status");
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
        //   participantsSection.getStyle().setBackground("purple");
          Span assigneesLabel = new Span("Assignees:");
          MultiSelectListBox<String> keysListBox = new MultiSelectListBox<>();
        //   keysListBox.setItems(project.getParticipants());
          List<String> friendsList = Arrays.asList("admin", "user1", "userx");
          keysListBox.setItems(friendsList);
          
          VerticalLayout dataSection = new VerticalLayout();
          dataSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
          dataSection.setSizeFull();
    
          Span status_collab = new Span();
          status_collab.add(status, collaborationMode);
          
          Span songs_audioDetails = new Span();
          VerticalLayout keysArea = new VerticalLayout();
          String keysListString = "";
          for (String key : project.getKeys()) {
            keysListString.concat(", " + key);
          }
          
          TextArea selectedKeys = new TextArea("Selected Keys");
          List<String> tempKeys = Arrays.asList("C", "C#", "Bb");
          selectedKeys.setReadOnly(true);
          
          MultiSelectComboBox<String> keysComboBox = new MultiSelectComboBox<>();
          keysComboBox.setLabel("Select keys:");
          keysComboBox.setItems(tempKeys);
          keysComboBox.addValueChangeListener(e -> {
              String selectedKeysText = e.getValue().stream().collect(Collectors.joining(", "));
              selectedKeys.setValue(selectedKeysText);
            });
            keysArea.add(keysComboBox);
            keysArea.add(selectedKeys);
            songs_audioDetails.add(keysArea);
            
            CircularProgressBar progressBar = new CircularProgressBar();
            progressBar.setCaption("Complete");
            progressBar.setWidthFull();
            progressBar.setHeightFull();
            progressBar.setPercent(project.getProgress());
            VerticalLayout progressBarContainer = new VerticalLayout();
            progressBarContainer.add(progressBar);
            
            dataSection.add(title, progressBarContainer, status_collab, songs_audioDetails);
            participantsSection.add(title, assigneesLabel, keysListBox);
            detailsSection.add(dataSection, participantsSection);
            // detailsSection.getStyle().setBackground("blue");
            
            // main container contining cards area and button
            VerticalLayout editingSection = new VerticalLayout();
            editingSection.setSizeFull();
            
            // trix rich text editor
            String editorInitialValue = "Add more details.";
            TrixEditor editor = new TrixEditor(editorInitialValue, "Description");
            editor.getStyle().setColor("white");
            editor.getStyle().setWidth("100%");
            editor.getStyle().setHeight("100%");
            // editor.getStyle().setBackground("red");
            
            editingSection.add(editor);
            
            editingSection.getStyle().setBackground("#006994"); // sea blue
            
            // project details and editing section 
            projectDetailsContainer.addToPrimary(taskGrid);
              projectDetailsContainer.addToSecondary(editingSection);
            projectDetailsContainer.setSplitterPosition(50);
            projectDetailsContainer.setOrientation(SplitLayout.Orientation.VERTICAL);
            
            
            // Set the orientation to horizontal (left, right)
            splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
            
            // Add the verticalLayout and the taskGrid as its children
            splitLayout.addToPrimary(detailsSection);
            splitLayout.addToSecondary(projectDetailsContainer);
            splitLayout.setSplitterPosition(30);
            
            // Set the split position to 50%
            splitLayout.setSplitterPosition(40);
            
            // Create a responsive navbar component
            NavBar navbar = new NavBar(customUserDetailsService, securityViewService);
            
            navbar.setContent(splitLayout);
            
            // Add it to the view
            this.removeAll();
            add(navbar);
        }
        
    }

  private Task generateRandomTask(int  i, Project project) {
      // Create and return a new Task object with the random values
      return new Task();
  }

    // Define a method to get a project by id
public Optional<Project> getProjectById(String id) {
    // Get the project from a mock data service
    return projectsService.findProjectById(id);
  }
  
}
