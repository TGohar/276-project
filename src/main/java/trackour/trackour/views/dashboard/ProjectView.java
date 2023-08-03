package trackour.trackour.views.dashboard;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.tatu.CircularProgressBar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.project.CollaborationMode;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.task.TaskService;
import trackour.trackour.model.task.TaskStatus;
import trackour.trackour.model.user.CustomUserDetailsService;
import trackour.trackour.model.user.FriendshipService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

@Route("project")
@RouteAlias("project-workspace")
// @PreserveOnRefresh
@PageTitle("Project Workspace | Trackour")
@PermitAll
public class ProjectView extends VerticalLayout implements BeforeEnterObserver ,HasUrlParameter<Long> {
  private Long projectId;

  @Autowired
  SecurityViewService securityViewService;

  @Autowired
  CustomUserDetailsService customUserDetailsService;

  @Autowired
  FriendshipService friendshipService;
    
  @Autowired
  private ProjectsService projectsService;

  @Autowired
  private TaskService taskService;

  private Grid<Task> grid;
  private Grid<Double> progressGrid;

  // Create the fields for the task properties
  private TextField titleField;
  private TextArea descriptionField;

  // private Select<TaskStatus> statusField;
  private Binder<Task> binder;
  private Binder<Task> editBinder;

  // Use @Autowired on the constructor parameters instead of the fields
  @Autowired
  public ProjectView(
    SecurityViewService securityViewService,
    CustomUserDetailsService customUserDetailsService,
    FriendshipService friendshipService,
    ProjectsService projectsService,
    TaskService taskService
    ) {
      this.securityViewService = securityViewService;
      this.customUserDetailsService = customUserDetailsService;
      this.friendshipService = friendshipService;
      this.projectsService = projectsService;
      this.taskService = taskService;
      grid = new Grid<>();
      progressGrid = new Grid<>();
      titleField = new TextField("Title");
      descriptionField = new TextArea("Description");
      // statusField = new Select<>();
      binder = new Binder<>(Task.class);     
      editBinder = new Binder<>(Task.class); 
    }

    public void updateTasksGrid(Long id) {
        
        // Create a new project object with the user id as the owner
        grid.setItems(projectsService.getAllTasksByProject(id));
    }

    public void updateProgressGrid(Long id) {
        projectsService.updateProgress(id);

        // progressGrid = new Grid<>();

        progressGrid.removeAllColumns();
        
        // Create a new project object with the user id as the owner
        progressGrid.setItems(Arrays.asList(projectsService.getProgress(id)));
        
        progressGrid = generateProgressBarGrid(progressGrid, id);
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
      Span owner = new Span();
      Span status = new Span();

      Span status_collab = new Span();
      status_collab.add(status, collaborationMode);

      Span songs_audioDetails = new Span();

      VerticalLayout projectDetailsArea = new VerticalLayout();

      TextArea selectedKeys = new TextArea("Keys");
      selectedKeys.setReadOnly(true);
      // selectedKeys.setValue(null);
      TextArea ownerTArea = new TextArea("Owner");
      ownerTArea.setValue(projectsService.getOwner(projectId));
      ownerTArea.setReadOnly(true);
      owner.add(ownerTArea);
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
      
      if (project.getCollaborationMode().equals(CollaborationMode.SOLO)){
        projectDetailsArea = new VerticalLayout();
        projectDetailsArea.add(owner, selectedKeys, selectedBpm, selecteStatus, selectedCollab);
      }
      else {
        projectDetailsArea = new VerticalLayout();
        projectDetailsArea.add(owner, selectedKeys, selectedBpm, selecteStatus, selectedCollab, selectedParticipants);
      }
      songs_audioDetails.add(projectDetailsArea);
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
            // bind collabModeSelect to editBinder
            editBinder.forField(collabModeSelect)
            .bind(Task::getStatus, Task::setStatus);

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
                // get the current state of the task after status value is changed
                // repopulate that task item with the changes from task change event
                // modify tsk with new status
                // retreive that task bean
                System.out.println("editedTask stat: " + stat.name());
                
                tsk.setStatus(stat);
                taskService.updateTask(tsk);
                // save it to db
                // if (switchBetweenCompletedAndNotCompleted) {
                //   // if progress recalculation is needed recalculate progress
                // }
                projectsService.updateProgress(projectId);
                updateTasksGrid(projectId);
                updateProgressGrid(projectId);
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
          FormLayout editingSection = new FormLayout();
          editingSection.setSizeFull();
          TextArea editor = new TextArea();
          // String editorInitialValue = "Add more details.";
          editor.setWidthFull();
          editor.setLabel("Description");
          editor.setValue(task.getDescription());

          TextField titleEditor = new TextField();
          titleEditor.setLabel("Title");
          titleEditor.setValue(task.getTitle());
          titleEditor.setWidthFull();

          editBinder.forField(editor)
          .bind(Task::getDescription, Task::setDescription);

          // Bind the titleEditor field to the title property
          editBinder.forField(titleEditor)
          .withValidator(new StringLengthValidator("Please enter a title", 1, null))
          .bind(Task::getTitle, Task::setTitle);
          
          // set proj description to the ediotor's value and save/update on value change
          // proj
          Button saveEditButton = new Button("Save Edit", ev -> {
            System.out.println("new desc:" + editor.getValue());
            System.out.println("new title:" + editor.getValue());

            if (editBinder.validate().isOk()) {
              // loads the bean that binds the fields to the Task obj 
              Task editedTask = editBinder.getBean();
              try {
                // loads/writes the edit data from the fields to the bean
                editBinder.writeBean(editedTask);

                // save Task bean/obj to db
                taskService.updateTask(editedTask);
                
                // Update the grid with the latest project list
                updateTasksGrid(projectId);
                updateProgressGrid(projectId);
                
              } catch (ValidationException e) {
                e.printStackTrace();
              }

            }
          });
          editingSection.add(titleEditor, editor, saveEditButton);  
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

        // Add the fields to the form layout
        formLayout.add(titleField, descriptionField); //, statusField);

        // Create a horizontal layout for the buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();

        // Open the dialog when the add button is clicked
        Button addButton = new Button("Add new task");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
          // Create a new task instance with the project id
          Task task = new Task(project);
          // Set the bean instance to edit
          binder.setBean(task);
          // taskService.createNewTask(task);
          // Open the dialog
          dialog.open();
        });

        // Create a save button and add a click listener
        Button saveButton = new Button("Save New", e -> {
          if (binder.validate().isOk()) {
            Task task = binder.getBean();
            taskService.createNewTask(task);

            // translate binder's bean to editBinder
            editBinder.setBean(task);
            
            // Then populate the editing fields
            editBinder.readBean(task);

            // Update the grid with the latest project list
            updateTasksGrid(projectId);
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
            updateTasksGrid(projectId);
            updateProgressGrid(projectId);
        });
      topAreaOfGrid.add(filterField, addButton, deleteButton);
      // Add the filter field above the grid
      hLayout.add(topAreaOfGrid, grid);
      }
      return hLayout;
    }
  
    @Override
    public void setParameter(BeforeEvent event, Long id) {
      
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

        // User user = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername()).get();
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

        updateProgressGrid(id);
        
        progressBarContainer.add(progressGrid);

        progressGrid.setItems(Arrays.asList(projectsService.getProgress(projectId)));
        // progressBarContainer.add(progressBar);

        // grid area onleft
        dataSection.add(progressBarContainer);

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

        rightSplitLayout.add(topComponent);
        
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
