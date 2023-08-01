package trackour.trackour.views.dashboard;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.jhoffmann99.TrixEditor;
import org.vaadin.addons.tatu.CircularProgressBar;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteParameters;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

// View for the project workspace
@Route("project/")
@RouteAlias("project-workspace")
@PreserveOnRefresh
@PageTitle("Project Workspace | Trackour")
@PermitAll
public class ProjectView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<Long> {

@Autowired
    SecurityViewService securityViewService;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    ProjectsService projectsService;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
  
    @Override
    public void setParameter(BeforeEvent event, Long id) {

      // boolean projectExists = projectsService.getAllProjects().stream()
      // .anyMatch(project -> project.getId() == id);
      // System.out.println("idX: " + id);
      
      // check if the id is invalid
      if (!projectsService.projectExists(id)) {
          // 404 error page
          event.rerouteToError(new NotFoundException("Invalid id"), null);
      } 
      else {
          // do something with the id
          
      }

        User user = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername()).get();
        // set the layout to fill the whole page
        this.setSizeFull();

        // create a navbar with the services
        NavBar navbar = new NavBar(customUserDetailsService, securityViewService);

        // create and add a split layout with left side being orange and right side being purple
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        leftSection.setSizeFull();
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        rightSection.setSizeFull();


        // left section customization
        VerticalLayout dataSection = new VerticalLayout();
        dataSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dataSection.setSizeFull();
        H1 title = new H1(projectsService.getById(id) == null ? "No title" : projectsService.getById(id).getTitle());
        Span collaborationMode = new Span();
        Div audioDetails = new Div();
        Span status = new Span();

        CircularProgressBar progressBar = new CircularProgressBar();
        progressBar.setCaption("Complete");
        progressBar.setWidthFull();
        progressBar.setHeightFull();
        progressBar.setPercent(0);

        VerticalLayout progressBarContainer = new VerticalLayout();
        progressBarContainer.add(progressBar);

        Span status_collab = new Span();
        status_collab.add(status, collaborationMode);

        Span songs_audioDetails = new Span();

        VerticalLayout keysArea = new VerticalLayout();

        String keysListString = "";
        for (String key : Arrays.asList("C", "C#")) {
          keysListString.concat(", " + key);
        }

        TextArea selectedKeys = new TextArea("Selected Keys");
        List<String> tempKeys = Arrays.asList("C", "C#", "Bb");
        selectedKeys.setReadOnly(true);
        keysArea.add(selectedKeys);
        songs_audioDetails.add(keysArea);
        dataSection.add(title, progressBarContainer, status_collab, songs_audioDetails);

        Span participantsLabel = new Span("Participants:");

        MultiSelectListBox<String> participantsListBox = new MultiSelectListBox<>();
        // filter the list of all users by getUsername
        List<String> userFriendsUsernames = user.getFriendsWith().stream()
                  .map(User::getUsername)
                  .collect(Collectors.toList());
        // get the Set<User> of participants
        // Set<User> participants = projectsService.getAllParticipantsForProject(id);
        // // convert the Set<User> to a List<String> of usernames
        // List<String> participantUsernames = participants.stream()
        //     .map(User::getUsername) // map each User to their username
        //     .collect(Collectors.toList()); // collect the usernames into a List
        // // set default selected participants

        // right section customization
        // create another split layout for the right section
        SplitLayout rightSplitLayout = new SplitLayout();
        rightSplitLayout.setSizeFull();
        rightSplitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

        // create a vertical layout for the top component
        VerticalLayout topComponent = new VerticalLayout();
        topComponent.setSizeFull();
        
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
        // rightSection.add(editingSection);

        // add the top component and the editing section to the right split layout
        rightSplitLayout.addToPrimary(topComponent);
        rightSplitLayout.addToSecondary(editingSection);

        // add the right split layout to the right section
        rightSection.add(rightSplitLayout);

        
        // main content customization
        rightSection.add(dataSection);

        leftSection.add(dataSection);

        // set left and right sections
        splitLayout.addToPrimary(leftSection);
        splitLayout.addToSecondary(rightSection);        
        splitLayout.setSplitterPosition(37);
        splitLayout.getPrimaryComponent().getStyle().set("background-color", "olivegreen");
        splitLayout.getSecondaryComponent().getStyle().set("background-color", "purple");
        // set the split layout as the content of the navbar's AppLayout
        navbar.setContent(splitLayout);

        // add the navbar as the only child of the vertical layout
        removeAll();
        this.add(navbar);
    }
}




  
  // Define a method to get a project by id
// public Optional<Project> getProjectById(String id) {
//     // Get the project from a mock data service
//     return projectsService.findProjectById(id);
//   }

/**
 * 
 * // Initialize the UI components
          title = new H1();
          collaborationMode = new Span();
          audioDetails = new Div();
          status = new Span();
          
          // Get the project object by id from some data source
          UserDetails sessionObject = securityViewService.getAuthenticatedRequestSession();
          String sessionUsername = sessionObject.getUsername();
          Optional<User> userOptional = customUserDetailsService.getByUsername(sessionUsername);
          if(userOptional.isPresent()) {
            User user = userOptional.get();

            Project project = user.getOwnedProjects().stream()
            .filter(p -> p.getId() == id) // filter by id
            .findFirst() // get the first match
            .orElse(null);

            if (project != null) {

              Grid<Task> taskGrid = new Grid<>(Task.class);
                
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
              taskGrid.setColumns("title", "description", "status", "createdAt");
              taskGrid.setSelectionMode(Grid.SelectionMode.MULTI);
              
              // -----------------------------------------------------------------------------------------------------
              // Populate the grid with the ArrayList of tasks
  
              // project.getTas
              // project.getO
              // taskService.createNewTask(new Task(project.getId(), user.getUid()));
  
              // taskService.get
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
              List<Boolean> friendsList = customUserDetailsService.getAll().stream().map(usr -> user.getFriendRequests().contains(usr.getUid())).collect(Collectors.toList());
              // keysListBox.setItems(friendsList);
              
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

                // removeAll();
                VerticalLayout container = new VerticalLayout();
                container.add(new Text("Hello"));
                navbar.setContent(container);
                add(navbar);
            }
            
        }
 */