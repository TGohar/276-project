package trackour.trackour.views.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
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

    // Declare a grid to display the projects
    private Grid<Project> grid;

    NavBar navbar;

    VerticalLayout mainLayout;

    // Get the set of selected items from the grid
    Set<Project> selectedProjects;

public Dashboard(SecurityViewService securityViewService, CustomUserDetailsService customUserDetailsService, ProjectsService projectsService) {
    this.customUserDetailsService = customUserDetailsService;
    this.securityViewService = securityViewService;
    this.projectsService = projectsService;
    
    mainLayout = new VerticalLayout();
    navbar = new NavBar(customUserDetailsService, securityViewService);
    // Initialize the grid and set the column name
    grid = new Grid<>();
    
    // Add a column for the title
    grid.addColumn(Project::getTitle).setHeader("Title").setKey("title");

    // Add a column for the creation date
    // Use the grid's addColumn method with a LocalDateTimeRenderer object instead of a lambda expression
    // Specify the format and locale for the LocalDateTimeRenderer object
    grid.addColumn(new LocalDateTimeRenderer <>(Project::getCreatedAt, "yyyy-MM-dd HH:mm:ss", Locale.CANADA))
        .setHeader("Created at")
        .setKey("createdAt")
        .setSortable(true); // Set the sortable property to true for the column

    // Add a column for the progress
    grid.addColumn(project -> Double.toString(project.getProgress())).setHeader("Progress").setKey("progress");
    
    // Modify the title column to make it sortable
    grid.getColumnByKey("title").setSortable(true);
    
    // Modify the creation date column to make it resizable
    grid.getColumnByKey("createdAt").setResizable(true);

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
        // Return the Span object
        return statusSpan;
    })).setHeader("Status").setKey("status").setSortable(true);

    // Use a ComponentRenderer to create the Span component for each project
    grid.addColumn(new ComponentRenderer<>(project -> {
        // Create a new Span object and set its text to the project's collaboration mode value
        Span modeSpan = new Span(project.getCollaborationMode().getValue());
        // Use a switch statement to add the badge theme variants to the span according to the collaboration mode value
        switch (project.getCollaborationMode()) {
            case SOLO:
                modeSpan.getElement().getThemeList().add("badge contrast small");
                break;
            case TEAM:
                modeSpan.getElement().getThemeList().add("badge primary small");
                break;
        }
        // Return the Span object
        return modeSpan;
})).setHeader("Collaboration mode").setKey("collaborationMode").setSortable(true);


    
    // Modify the progress column to make it resizable
    grid.getColumnByKey("progress").setResizable(true);
    
    // Use a ComponentRenderer to create the button component for each project 
    grid.addColumn(new ComponentRenderer<>(project -> { 
        // Create a new Button object and set its text, icon, theme and click listener 
        Button button = new Button("Open", VaadinIcon.EXTERNAL_LINK.create()); 
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY); 
        button.addClickListener(e -> { 
            // Open the project view in a new tab with the project id as a parameter 
            UI.getCurrent().getPage().open("project/" + project.getId(), "_blank"); }); 
            // Wrap the button object in a Div component and return it 
            Div div = new Div(button); 
            return div; 
    })).setHeader("Open").setKey("open");

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
        Set<Project> selectedProjects = grid.getSelectedItems();
        // Iterate over the set and delete each project from the database using the service
        for (Project project : selectedProjects) {
            System.out.println("delete proj " + project.getId());
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
    titleField.addKeyPressListener(Key.ENTER, e -> {
        // Call the createButton's click listener
        createButton.click();
    });
    createButton.addClickListener(e -> {
        // Get the value from the text field
        String title = titleField.getValue();
        // Get the user object from the service
        Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
        User user = userOptional.get();
        // Create a new project object with the user id as the owner
        Project newProj = new Project(user);
        // // Set the title of the new project with the value from the text field
        newProj.setTitle(title);
        // // Save the new project to the database using the service
        projectsService.createNewProject(newProj);
        // Update the grid with the new project list
        updateGrid();
        // Close the dialog or hide the layout
        newProjectDialog.close();
    });

    newProjectDialog.add(titleField, createButton);

    // Modify the add new project button's click listener to open the dialog or show the layout instead of creating directly
    addButton.addClickListener(e -> {
        // Open the newProjectDialog or show the layout
        newProjectDialog.open();
    });

    // Enable the grid's multi-select mode
    grid.setSelectionMode(SelectionMode.MULTI);

    // Listen to the grid's selection change event and update the dialogMessage and dialogTitle variables accordingly
    grid.addSelectionListener(e -> {
        // Get the set of selected items from the grid
        Set<Project> selectedProjects = grid.getSelectedItems();
        
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
        Set<Project> selectedProjects = grid.getSelectedItems();
        // Open the dialog if there are any selected items, otherwise show a notification
        if (!selectedProjects.isEmpty()) {
            dialog.open();
        } else {
            Notification.show("No projects selected for deletion");
        }
    });

    // Add the grid and the button to the layout
    mainLayout.add(dialog, grid, addButton, deleteButton);
    // Add it to the view
    add(navbar);
    // Add some content below the navbar
    navbar.setContent(mainLayout);
    
    // Update the grid with the current projects
    updateGrid();
}

// A method to update the grid with the latest projects from the database
private void updateGrid() {
    // Get the user object from the service
    Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());
    User user = userOptional.get();

    List<Project> projects = new ArrayList<>();
    
    if (userOptional.isPresent()){
        projects = projectsService.getAllByOwner(user);
    
        System.out.println("projects size: " + projects.size());
    
        // Get the list of projects owned by the user from the service
        // List<Project> projects = user.getOwnedProjects();
        // List<Project> projects = projectsService.getAllByOwner(user.getUid());
    
        // Set the grid items to the project list
        grid.setItems(projects);
    }
}
}


