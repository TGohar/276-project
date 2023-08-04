package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.project.CollaborationMode;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.task.TaskService;
import trackour.trackour.model.user.CustomUserDetailsService;
import trackour.trackour.model.user.FriendshipService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.camelotwheel.Camelot;
import trackour.trackour.views.components.camelotwheel.Key;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;
// import com.vaadin.flow.component.badge.Badge;

@Route("dashboard")
@RouteAlias("dash")
@PageTitle("Dashboard | Trackour")
@PreserveOnRefresh
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the "admin" role specified as well
@PermitAll
public class Dashboard extends MyBlockResponsiveLayout{
    @Autowired
    SecurityViewService securityViewService;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ProjectsService projectsService;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    TaskService taskService;

    // Declare a grid to display the projects
    private Grid<Project> ownedGrid;
    private Grid<Project> participatedGrid;

    NavBar navbar;

    VerticalLayout mainLayout;

    // Get the set of selected items from the grid
    Set<Project> selectedProjects;

public Dashboard(
    SecurityViewService securityViewService, 
    CustomUserDetailsService customUserDetailsService, 
    ProjectsService projectsService,
    FriendshipService friendshipService,
    TaskService taskService
    ) {
    this.customUserDetailsService = customUserDetailsService;
    this.securityViewService = securityViewService;
    this.projectsService = projectsService;
    this.friendshipService = friendshipService;

    mainLayout = new VerticalLayout();
    navbar = new NavBar(customUserDetailsService, securityViewService);
    // Initialize the grid and set the column name
    ownedGrid = new Grid<>();
    participatedGrid = new Grid<>();
    
    generateGrid(ownedGrid);
    generateGrid(participatedGrid);

    // participatedGrid
    // Remove the selectedProjects variable from the class

    // Create a new string variable to store the dialog's message
    String dialogMessage = "Are you sure you want to delete 0 projects?";

    // Create a new string variable to store the dialog's title
    String dialogTitle = "Confirm deletion";

    // Create a new Text object with the dialogMessage variable
    Text deleteMessage = new Text(dialogMessage);

    // Create a new ConfirmationDialog object and set its title, message and buttons
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle(dialogTitle);
    dialog.add(deleteMessage);
    dialog.add(new Button("Delete", e -> {
        // Get the set of selected items from the grid
        Set<Project> selectedProjects = ownedGrid.getSelectedItems();
        // Iterate over the set and delete each project from the database using the service
        for (Project project : selectedProjects) {
            System.out.println("delete proj " + project.getId());
            taskService.deleteTasksByProject(project);
            projectsService.deleteProject(project);
        }
        // Update the grid with the latest project list
        updateGrid();
        // Close the dialog
        dialog.close();
    }));
    dialog.add(new Button("Cancel", e -> {
        // Close the dialog without deleting anything
        dialog.close();
    }));

    // Add the dialog to the layout
    mainLayout.add(dialog);

    // Initialize the button and add a click listener
    Button addButton = new Button("Add new project");
    addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    // addButton.addClickListener(e -> addNewProject());

    // Create a new TextField object and set its label and placeholder
    TextField titleField = new TextField();
    titleField.setWidthFull();
    titleField.focus();
    titleField.setLabel("Project title");
    titleField.setPlaceholder("Enter a title for your project");

    // Create a new Button object and set its text and click listener
    // Add the text field and the button to a new dialog or a new layout
    Dialog newProjectDialog = new Dialog();
    
    Button createButton = new Button("Create project");
    // Use the addKeyPressListener method to listen for the enter key on the titleField
    titleField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> {
        // Call the createButton's click listener
        createButton.click();
    });

    // create a select component
    Select<CollaborationMode> collabModeSelect = new Select<>();

    // set the items from the enum values
    collabModeSelect.setItems(EnumSet.allOf(CollaborationMode.class));

    // set the label generator to use the getValue() method
    collabModeSelect.setItemLabelGenerator(CollaborationMode::getValue);
    collabModeSelect.setValue(CollaborationMode.SOLO);

    createButton.addClickListener(e -> {
        // Get the value from the text field
        String title = titleField.getValue();
        // Get the user object from the service
        Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
        User user = userOptional.get();
        // Create a new project object with the user id as the owner
        Project newProj = new Project(user);
        // set collab mode
        newProj.setCollaborationMode(collabModeSelect.getValue());
        // // Set the title of the new project with the value from the text field
        newProj.setTitle(title);
        // // Save the new project to the database using the service
        projectsService.createNewProject(newProj);
        // Update the grid with the new project list
        updateGrid();
        // Close the dialog or hide the layout
        newProjectDialog.close();
    });

    newProjectDialog.add(titleField, collabModeSelect, createButton);

    // Modify the add new project button's click listener to open the dialog or show the layout instead of creating directly
    addButton.addClickListener(e -> {
        // Open the newProjectDialog or show the layout
        newProjectDialog.open();
    });

    // Enable the grid's multi-select mode
    ownedGrid.setSelectionMode(SelectionMode.MULTI);

    // Listen to the grid's selection change event and update the dialogMessage and dialogTitle variables accordingly
    ownedGrid.addSelectionListener(e -> {
        // Get the set of selected items from the ownedGrid
        Set<Project> selectedProjects = ownedGrid.getSelectedItems();
        
        // Update the deleteMessage text with the dialogMessage variable
        deleteMessage.setText("Are you sure you want to delete " + selectedProjects.size() + " projects?");
        
        // Update the dialog's header title with the dialogTitle variable
        dialog.setHeaderTitle("Confirm deletion (" + selectedProjects.size() + ")");
    });

    // Create a new button object and set its text and click listener
    Button deleteButton = new Button("Delete selected projects");
    deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
    deleteButton.addClickListener(e -> {
        // Get the set of selected items from the grid
        Set<Project> selectedProjects = ownedGrid.getSelectedItems();
        // Open the dialog if there are any selected items, otherwise show a notification
        if (!selectedProjects.isEmpty()) {
            dialog.open();
        } else {
            Notification.show("No projects selected for deletion");
        }
    });

    VerticalLayout ownedProjects = new VerticalLayout();
    ownedProjects.setSizeFull();
    VerticalLayout participatedProjects = new VerticalLayout();
    participatedProjects.setSizeFull();
    
    // Add the ownedGrid and the button to the layout
    // dialog, ownedGrid, addButton, deleteButton
    Text ownedProjectsHeader = new Text("Owned");
    Text partProjectsHeader = new Text("Participated");
    ownedProjects.add(ownedProjectsHeader, dialog, ownedGrid, addButton, deleteButton);
    participatedProjects.add(partProjectsHeader, participatedGrid);
    SplitLayout splitLayout = new SplitLayout();
    splitLayout.setSizeFull();
    splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

    // set left and right sections
    splitLayout.addToPrimary(ownedProjects);
    splitLayout.addToSecondary(participatedProjects);        
    splitLayout.setSplitterPosition(90);
    
    // set the split layout as the content of the navbar's AppLayout
    mainLayout.add(splitLayout);
    navbar.setContent(splitLayout);
    // Add it to the view
    add(navbar);
    
    // Update the grid with the current projects
    updateGrid();
}

private void generateGrid(Grid<Project> grid) {
    Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
    User user = userOptional.get();
    // Add a column for the title
    grid.addColumn(Project::getTitle).setHeader("Title").setKey("title");
    grid.getColumnByKey("title").setSortable(true);

    // Add a column for the creation date
    grid.addColumn(new LocalDateTimeRenderer <>(Project::getCreatedAt, "yyyy-MM-dd HH:mm:ss", Locale.CANADA))
        .setHeader("Created at")
        .setKey("createdAt")
        .setSortable(true); // Set the sortable property to true for the column
    // Use a ComponentRenderer to create the Span component for each project
    grid.addColumn(new ComponentRenderer<>(project -> {
        // Create a new Span object and set its text to the project's status value
        Span statusSpan = new Span(project.getStatus().getValue());
        // Use a switch statement to add the badge theme variants to the span according to the status value
        switch (project.getStatus()) {
            case COMPLETED:
                statusSpan.getElement().getThemeList().add("badge success small");
                break;
            case IN_PROGRESS:
                statusSpan.getElement().getThemeList().add("badge primary small");
                break;
        }

        return statusSpan;
    })).setHeader("Status").setKey("status").setSortable(true);
    // Use a ComponentRenderer to create the Span component for each project
    grid.addColumn(new ComponentRenderer<>(project -> {

        Select<CollaborationMode> collabModeSelect = new Select<>();

        // set the items from the enum values
        collabModeSelect.setItems(EnumSet.allOf(CollaborationMode.class));

        // set the label generator to use the getValue() method
        collabModeSelect.setItemLabelGenerator(CollaborationMode::getValue);
        collabModeSelect.setValue(project.getCollaborationMode());
        
        collabModeSelect.addValueChangeListener(event -> {
            // get the new selected value
            CollaborationMode mode = event.getValue();
        
            // show a notification with the new value
            Notification.show("Selected mode: " + mode.getValue());
            // update
            project.setCollaborationMode(mode);
            projectsService.updateProject(project);
        });

        User projectOwner = project.getOwner();

        Span collab = new Span();
        TextArea selecteStatus = new TextArea();
        selecteStatus.setReadOnly(true);
        String statStr = collabModeSelect.getValue().getValue();
        selecteStatus.setValue(statStr);
        collab.add(selecteStatus);

        // Return the Span object
        if (!projectOwner.getUid().equals(user.getUid())){
            return collab;
        }
        return collabModeSelect;
        // Return the Span object
    })).setHeader("Collaboration mode").setKey("collaborationMode").setSortable(true);
    //
    grid.addColumn(new ComponentRenderer<>(proj -> {
        
        // ---------------------------------
        Set<Long> parts = projectsService.getAllParticipantIdsForProject(proj.getId());
        System.out.println("participants size: " + parts.size());
        
        MultiSelectComboBox<Long> collabModeSelect = new MultiSelectComboBox<>();

        // set the items from the enum values
        collabModeSelect.setItems(friendshipService.getFriendsUids(user.getUid()));

        // set the label generator to use the getValue() method
        collabModeSelect.setItemLabelGenerator(k -> customUserDetailsService.getByUid(k).get().getUsername());
        collabModeSelect.setValue(parts);

        // Add a value change listener to the multiselectcombobox
        collabModeSelect.addValueChangeListener(event -> {
            // Get the old and new values of the multiselectcombobox
            // Set<User> oldValue = event.getOldValue();
            Set<Long> newValue = event.getValue();
            projectsService.setParticipantsByLong(newValue, proj);
        });

        User projectOwner = proj.getOwner();

        Span collab = new Span();
        TextArea selecteStatus = new TextArea();
        selecteStatus.setReadOnly(true);
        Set<Long> values = collabModeSelect.getValue();
        // Convert the set to a list using a stream
        List<String> list = values.stream().map(x->x.toString()).collect(Collectors.toList());
        // Join the list elements with commas using String.join()
        String statStr = String.join(", ", list);
        selecteStatus.setValue(statStr);
        collab.add(selecteStatus);

        // Return the Span object
        if (!projectOwner.getUid().equals(user.getUid())){
            return collab;
        }
        // Return the Span object
        return collabModeSelect;
    })).setHeader("Participants");
    //
    grid.addColumn(new ComponentRenderer<>(proj -> {

        Set<Key> parts = proj.getKeys();
        // Set<User> partsUsers = parts.stream()
        // .map(p -> customUserDetailsService.getByUid(p))
        // .filter(Optional::isPresent)
        // .map(Optional::get)
        // .collect(Collectors.toSet());

        MultiSelectComboBox<Key> collabModeSelect = new MultiSelectComboBox<>();

        // set the items from the enum values
        collabModeSelect.setItems(Camelot.getAllKeys());

        // set the label generator to use the getValue() method
        collabModeSelect.setItemLabelGenerator(k -> k.name);
        collabModeSelect.setValue(parts);
        
        // Get the selected users from the multiselectcombobox
        Set<Key> selectedUsers = collabModeSelect.getValue();

        // Do something with the selected users
        // For example, print their usernames
        for (Key key : selectedUsers) {
            System.out.println(key.name);
        }

        // Add a value change listener to the multiselectcombobox
        collabModeSelect.addValueChangeListener(event -> {
            // Get the old and new values of the multiselectcombobox
            // Set<User> oldValue = event.getOldValue();
            Set<Key> newValue = event.getValue();

            // add some users to the set
            String keys = newValue.stream()
            .map(key -> key.name) // convert each user to a string
            .collect(Collectors.joining(", ")); // join them with commas
            Notification.show("Selected keys: " + keys);
            projectsService.setKeys(newValue, proj);
        });

        User projectOwner = proj.getOwner();

        Span collab = new Span();
        TextArea selecteStatus = new TextArea();
        selecteStatus.setReadOnly(true);
        Set<Key> values = collabModeSelect.getSelectedItems();
        // Convert the set to a list using a stream
        List<String> list = values.stream().map(x->x.name).collect(Collectors.toList());
        // Join the list elements with commas using String.join()
        String statStr = String.join(", ", list);
        selecteStatus.setValue(statStr);
        collab.add(selecteStatus);

        // Return the Span object
        if (!projectOwner.getUid().equals(user.getUid())){
            return collab;
        }
        // Return the Span object
        return collabModeSelect;
    })).setHeader("Keys:");


    // Use a ComponentRenderer to create the button component for each project 
    grid.addColumn(new ComponentRenderer<>(project -> { 
            // Create a new Button object and set its text, icon, theme and click listener 
            IntegerField adultsField = new IntegerField();
            adultsField.setValue(projectsService.getBpm(project.getId()));
            adultsField.setStepButtonsVisible(true);
            adultsField.setMin(0);

            adultsField.addValueChangeListener(ev -> {
                // projectsService
                Integer newValue = ev.getValue();
                project.setBpm(newValue);
                projectsService.updateProject(project);
            });
            return adultsField; 
    })).setHeader("Bpm").setKey("bpm");

    // Use a ComponentRenderer to create the button component for each project 
    grid.addColumn(new ComponentRenderer<>(project -> { 
        // Create a new Button object and set its text, icon, theme and click listener 
        Button button = new Button("Open", VaadinIcon.EXTERNAL_LINK.create()); 
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY); 
        button.addClickListener(e -> { 
            // Open the project view in a new tab with the project id as a parameter 
            // QueryParameters queryParameters = QueryParameters.simple(Map.of("query", searchValue));
            //         ui.navigate("search", queryParameters);
            UI.getCurrent().getPage().open("project/" + project.getId(), "_blank"); }); 
            // Wrap the button object in a Div component and return it 
            Div div = new Div(button); 
            return div; 
    })).setHeader("Open").setKey("open");
}

// A method to update the grid with the latest projects from the database
private void updateGrid() {
    // Get the user object from the service
    Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
    User user = userOptional.get();

    List<Project> projects = new ArrayList<>();
    List<Project> participatedProjects = new ArrayList<>();
    
    if (userOptional.isPresent()){
        // get the projects you own
        projects = projectsService.getAllByOwner(user);
        participatedProjects = projectsService.getAllByParticipation(user);
        // as well as the ones you don't own but  participated in
    
        System.out.println("projects size: " + projects.size());
        System.out.println("participatedProjects size:" + participatedProjects.size());
    
        // Set the grid items to the project list
        ownedGrid.setItems(projects);
        participatedGrid.setItems(participatedProjects);
    }
}
}