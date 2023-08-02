package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.addons.tatu.CircularProgressBar;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.task.TaskService;
import trackour.trackour.model.task.TaskStatus;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

@Route("project")
@RouteAlias("project-workspace")
@PreserveOnRefresh
@PageTitle("Project Workspace | Trackour")
@PermitAll
public class ProjectView extends VerticalLayout implements BeforeEnterObserver ,HasUrlParameter<Long> {
  private Long projectId;

  private SecurityViewService securityViewService;
    
  private CustomUserDetailsService customUserDetailsService;
    
  private ProjectsService projectsService;

  private TaskService taskService;

  private Grid<Task> grid;
  private Grid<Double> progressGrid;

  // Create the fields for the task properties
  private TextField titleField;
  private TextArea descriptionField;

  private Select<TaskStatus> statusField;
  private Binder<Task> binder;

  // Use @Autowired on the constructor parameters instead of the fields
  @Autowired
  public ProjectView(SecurityViewService securityViewService,
    CustomUserDetailsService customUserDetailsService,
    ProjectsService projectsService,
    TaskService taskService
    ) {
      this.securityViewService = securityViewService;
      this.customUserDetailsService = customUserDetailsService;
      this.projectsService = projectsService;
      this.taskService = taskService;
      grid = new Grid<>();
      progressGrid = new Grid<>();
      titleField = new TextField("Title");
      descriptionField = new TextArea("Description");
      statusField = new Select<>();
      binder = new Binder<>(Task.class);      
    }

    public void updateGrid(Long id) {
        // Get the user object from the service
        Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
        // User user = userOptional.get();
        
        // Create a new project object with the user id as the owner
        grid.setItems(projectsService.getAllTasksByProject(id));
        grid.getDataProvider().refreshAll();
    }

    public void updateProgressGrid(Long id) {
        // Get the user object from the service
        Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
        // User user = userOptional.get();
        projectsService.updateProgress(id);
        
        // Create a new project object with the user id as the owner
        progressGrid.setItems(Arrays.asList(projectsService.getProgress(id)));
        progressGrid.getDataProvider().refreshAll();
        // UI.getCurrent().getPage().reload();
    }

    private static Renderer<Task> createToggleDetailsRenderer(
        Grid<Task> grid) {
    return LitRenderer.<Task> of(
            "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Edit</vaadin-button>")
            .withFunction("handleClick",
                    task -> grid.setDetailsVisible(task,
                            !grid.isDetailsVisible(task)));
        }

    private Grid<Double> generateProgressBarGrid(Grid<Double> progressGrid, Long projectId) {

      // projectsService.getAllTasksByProject(id)
      // VerticalLayout progressBarContainer = new VerticalLayout();
      // progressBarContainer.setSizeFull();

      Project project = projectsService.getById(projectId);

      H1 title = new H1(projectsService.getById(projectId) == null ? "No title" : projectsService.getById(projectId).getTitle());
      Span collaborationMode = new Span();
      Div audioDetails = new Div();
      Span status = new Span();

      Span status_collab = new Span();
      status_collab.add(status, collaborationMode);

      Span songs_audioDetails = new Span();

      VerticalLayout keysArea = new VerticalLayout();

      // String keysListString = "";
      // for (String key : selectedKeys) { // Use the injected list of keys
      //   keysListString.concat(", " + key);
      // }

      TextArea selectedKeys = new TextArea("Keys");
      selectedKeys.setReadOnly(true);
      // selectedKeys.setValue(null);
      TextArea selectedBpm = new TextArea("BPM");
      selectedBpm.setReadOnly(true);
      Set<String> bpmStr = projectsService.getAllParticipantIdsForProject(projectId).stream()
        .map(p -> customUserDetailsService.getByUid(p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(p -> p.getUsername())
        .collect(Collectors.toSet());
      selectedBpm.setValue(String.join(", ", bpmStr));

      TextArea selecteStatus = new TextArea("Status");
      selecteStatus.setReadOnly(true);
      String statStr = projectsService.findProjectById(projectId).get().getStatus().getValue();
      selecteStatus.setValue(statStr);

      TextArea selectedCollab = new TextArea("Collab Mode");
      selectedCollab.setReadOnly(true);
      String collabStr = projectsService.findProjectById(projectId).get().getCollaborationMode().getValue();
      selectedCollab.setValue(collabStr);
      
      TextArea selectedParticipants = new TextArea("Participants");
      selectedParticipants.setReadOnly(true);
      Set<String> partsUsers = projectsService.getAllParticipantIdsForProject(projectId).stream()
        .map(p -> customUserDetailsService.getByUid(p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(p -> p.getUsername())
        .collect(Collectors.toSet());
      selectedParticipants.setValue(String.join(", ", partsUsers));
      
      // keysArea.add(selectedKeysArea);
      keysArea.add(selectedKeys, selectedBpm, selecteStatus, selectedCollab, selectedParticipants);
      songs_audioDetails.add(keysArea);
      // keys 
      // status
      // collaboration mode
      // participants

      if (project != null) {
        double progresD = projectsService.getProgress(projectId);
        progressGrid.setItems(Arrays.asList(progresD));

         // ad progress bar column
        progressGrid.addColumn(new ComponentRenderer<>(tsk -> {
          VerticalLayout container = new VerticalLayout();
          grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
          CircularProgressBar cprogressBar = new CircularProgressBar();
          cprogressBar.setCaption("Complete");
          cprogressBar.getStyle().setWidth("12rem");
          cprogressBar.getStyle().setHeight("12rem");
          cprogressBar.setPercent(progresD);
          container.add(cprogressBar, title, status_collab, songs_audioDetails);
          return container;
        }));
      }
      return progressGrid;
    }

    private boolean userIsAuthorizedToAccessProject(Long projectId) {
      Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
      if (userOptional.isPresent()) {
        User user = userOptional.get();
        if (projectsService.isValidParticipant(user.getUid(), projectId)){
          return true;
        }
      }
      return false;
    }

    private VerticalLayout generateTasksGrid(Grid<Task> grid, Long projectId) {

      // projectsService.getAllTasksByProject(id)
      VerticalLayout hLayout = new VerticalLayout();
      hLayout.setSizeFull();

      Project project = projectsService.getById(projectId);

      if (project != null) {
        grid.setSizeFull();
        grid.addColumn(trackour.trackour.model.task.Task::getTitle).setHeader("Title");
        // Use a ComponentRenderer to create the Span component for each project
        grid.addColumn(new ComponentRenderer<>(tsk -> {

            Select<TaskStatus> collabModeSelect = new Select<>();

            // set the items from the enum values
            collabModeSelect.setItems(EnumSet.allOf(TaskStatus.class));

            // set the label generator to use the getValue() method
            collabModeSelect.setItemLabelGenerator(TaskStatus::getValue);
            collabModeSelect.setValue(tsk.getStatus());
            
            collabModeSelect.addValueChangeListener(event -> {
                // get the new selected value
                TaskStatus stat = event.getValue();
            
                // show a notification with the new value
                Notification.show("Selected status: " + stat.getValue()); // Use Notification instead of System.out.println
                // update
                // stat change
                boolean oldStatusIsCompleted = tsk.getStatus().equals(TaskStatus.COMPLETED);
                boolean newStatusIsCompleted = stat.equals(TaskStatus.COMPLETED);
                boolean switchBetweenCompletedAndNotCompleted = (oldStatusIsCompleted && !newStatusIsCompleted) || 
                (!oldStatusIsCompleted && newStatusIsCompleted);
                if (switchBetweenCompletedAndNotCompleted) {
                  tsk.setStatus(stat);
                  taskService.updateTask(tsk);
                  projectsService.updateProgress(projectId);
                  // updateProgressGrid(projectId);
                  updateGrid(projectId);
                  updateProgressGrid(projectId);
                  UI.getCurrent().getPage().reload();
                }
            });
            // Return the Span object
            return collabModeSelect;
        })).setHeader("Status");

        // Make the tasks selectable
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        // editingSection for editing
        grid.addColumn(createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(task ->  {
        VerticalLayout editingSection = new VerticalLayout();
          editingSection.setSizeFull();
          // trix rich text editor
          TextArea editor = new TextArea();
          String editorInitialValue = "Add more details.";
          editor.setWidthFull();
          editor.setLabel("Description");
          editor.setValue(task.getDescription());

          TextField titleEditor = new TextField();
          titleEditor.setLabel("Title");
          titleEditor.setValue(task.getTitle());
          titleEditor.setWidthFull();
          // set proj description to the ediotor's value and save/update on value change
          // proj
          Button saveDescriptionButton = new Button("Save", e -> {
            // Get the current task from the binder
            // Task task = binder.getBean();
            // binder.readBean(task);
            System.out.println("new desc:" + editor.getValue());
            System.out.println("new title:" + editor.getValue());

            task.setDescription(editor.getValue());
            task.setTitle(titleEditor.getValue());
            // // Save or update the task using the service
            taskService.createNewTask(task);
            updateGrid(projectId);
            updateProgressGrid(projectId);
            // taskService.createNewTask(task);
          });
          editingSection.add(titleEditor, editor, saveDescriptionButton);  
          return editingSection;      
      }));
  
        // Add any other features you think it needs
        // For example, you can add a filter field to search by title
        HorizontalLayout topAreaOfGrid = new HorizontalLayout();
        topAreaOfGrid.setWidthFull();
        TextField filterField = new TextField();
        filterField.setPlaceholder("Filter by title");
        filterField.addValueChangeListener(event -> {
          // Set some items for the grid
          List<Task> tasks = projectsService.getAllTasksByProject(projectId);
        // as well as the ones you don't own but  participated in
    
        System.out.println("projects size: " + tasks.size());
          grid.setItems(projectsService.getAllTasksByProject(projectId));
          // Get the filter value
          String filter = event.getValue();
          // Filter the tasks by title
          List<Task> filteredTasks = tasks.stream()
            .filter(tsk -> tsk.getTitle().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList());
          // Set the filtered tasks to the grid
          grid.setItems(filteredTasks);
        });
        
        // Create a dialog instance for adding or editing tasks
        Dialog dialog = new Dialog();

        // Create a form layout for the task fields
        FormLayout formLayout = new FormLayout();

        // Create a binder instance
        // Bind the fields to the binder
        binder.forField(titleField)
          .withValidator(new StringLengthValidator(
            "Please enter a title", 1, null))
          .bind(Task::getTitle, Task::setTitle);
        binder.forField(descriptionField)
          .bind(Task::getDescription, Task::setDescription);
        // binder.forField(editor)
        //   .bind(Task::getDescription, Task::setDescription);
        // binder.forField(statusField)
        //   // .withValidator(Objects::nonNull, "Please select a status")
        //   .bind(Task::getStatus, Task::setStatus);

        // String value = event.getValue();
        
        // taskService.createNewTask(null);

        // Add the fields to the form layout
        formLayout.add(titleField, descriptionField); //, statusField);

        // Create a horizontal layout for the buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();

        // Create a save button and add a click listener
        Button saveButton = new Button("Save", e -> {
          // Validate and save the task using the binder
          if (binder.validate().isOk()) {
            Task task = binder.getBean();
            binder.readBean(task);
            taskService.createNewTask(task);
            // taskService.saveTask(task);
            // Update the grid with the latest task list
            updateGrid(projectId);
            updateProgressGrid(projectId);
            // Close the dialog
            dialog.close();
          }
        });

        // Create a cancel button and add a click listener
        Button cancelButton = new Button("Cancel", e -> {
          // Close the dialog
          dialog.close();
        });

        // Add the buttons to the button layout
        buttonLayout.add(saveButton, cancelButton);

        // Add the form layout and the button layout to the dialog
        dialog.add(formLayout, buttonLayout);

        // Open the dialog when the add button is clicked
        Button addButton = new Button("Add new task");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
          // Create a new task instance with the project id
          Task task = new Task(project);
          // Set the bean instance to edit
          binder.setBean(task);
          taskService.createNewTask(task);
          // Open the dialog
          dialog.open();
        });
        
        // Create a delete button and add a click listener
        Button deleteButton = new Button("Delete", e -> {
            // Get the set of selected items from the grid
            Set<Task> selectedTasks = grid.getSelectedItems();
            // Iterate over the set and delete each project from the database using the service
            for (Task delTask : selectedTasks) {
                // Show a notification when a task is deleted
                Notification.show("Task deleted successfully");
                taskService.deleteTask(delTask);
            }
            // Update the grid with the latest project list
            updateGrid(projectId);
            updateProgressGrid(projectId);
        });
      topAreaOfGrid.add(filterField, addButton, deleteButton);
      // Add the filter field above the grid
      hLayout.add(topAreaOfGrid, grid);
      }
      return hLayout;
    }
  
    // Use @PostConstruct to initialize the view after dependency injection
    // @Override
    // public void setParameter(BeforeEvent event, Long id) {
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
      if (!userIsAuthorizedToAccessProject(id)) {
        event.rerouteToError(new NotFoundException("Invalid id"), null);
      }
      // Use projectId as needed
      this.projectId = id;

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

        CircularProgressBar progressBar = new CircularProgressBar();
        progressBar.setCaption("Complete");
        progressBar.getStyle().setWidth("12rem");
        progressBar.getStyle().setHeight("12rem");
        progressBar.setPercent(projectsService.getProgress(projectId));

        VerticalLayout progressBarContainer = new VerticalLayout();

        progressBarContainer.setSizeFull();

        progressBarContainer.removeAll();

        progressGrid = new Grid<>();

        progressGrid = generateProgressBarGrid(progressGrid, projectId);
        
        progressBarContainer.add(progressGrid);

        progressGrid.setItems(Arrays.asList(projectsService.getProgress(projectId)));
        // progressBarContainer.add(progressBar);

        // grid area onleft
        dataSection.add(progressBarContainer);

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
        VerticalLayout rightSplitLayout = new VerticalLayout(); // Use VerticalSplitLayout instead of SplitLayout
        rightSplitLayout.setSizeFull();

        // create a vertical layout for the top component
        VerticalLayout topComponent = new VerticalLayout();
        topComponent.setSizeFull();

        topComponent.removeAll();

        grid = new Grid<>();
        
        topComponent.add(generateTasksGrid(grid, projectId));

        grid.setItems(projectsService.getAllTasksByProject(projectId));

        // rightSection.add(editingSection);

        rightSplitLayout.add(topComponent);
        // add the top component and the editing section to the right split layout
        // rightSplitLayout.addToPrimary(topComponent);
        // rightSplitLayout.addToSecondary(new Span("Empty"));

        
        VerticalLayout socialSection = new VerticalLayout();
        socialSection.setSizeFull();
        socialSection.getStyle().setBackground("green");
        socialSection.add(new Span("Right section"));
        
        leftSection.add(dataSection);
        // add the right split layout to the right section
        rightSection.add(rightSplitLayout);

        // set left and right sections
        splitLayout.addToPrimary(leftSection);
        splitLayout.addToSecondary(rightSection);        
        splitLayout.setSplitterPosition(37);
        
        // set the split layout as the content of the navbar's AppLayout
        navbar.setContent(splitLayout);

        // add the navbar as the only child of the vertical layout
        removeAll();
        this.add(navbar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {}
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