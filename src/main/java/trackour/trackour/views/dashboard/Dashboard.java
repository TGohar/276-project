package trackour.trackour.views.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.project.ProjectsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route("dashboard")
@RouteAlias("dash")
@PreserveOnRefresh
@PageTitle("Dashboard | Trackour")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@PermitAll
public class Dashboard extends MyBlockResponsiveLayout{
    
    @Autowired
    SecurityViewService securityViewHandler;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ProjectsService projectsService;

    MockDataService mockDataService;
    
    public Dashboard(
        SecurityViewService securityViewHandler,
        CustomUserDetailsService customUserDetailsService,
        ProjectsService projectsService) {
        
        this.mockDataService = MockDataService.getInstance();
        // init args
        this.securityViewHandler = securityViewHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.projectsService = projectsService;

        Optional<User> userOptional = customUserDetailsService.getByUsername(securityViewHandler.getAuthenticatedRequestSession().getUsername());

        // main container contining cards area and button
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setSizeFull();

        Scroller scroller = new Scroller();
        scroller.setSizeFull();

        // conteiner for cards
        final FlexLayout cardsFlexLayout = new FlexLayout();
        cardsFlexLayout.setSizeFull();
        cardsFlexLayout.getStyle().set("display", "flex");
        cardsFlexLayout.getStyle().set("flex-wrap", "wrap");
        cardsFlexLayout.setHeightFull();
        cardsFlexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // add button
        Button addButton = new Button("Add new project");
        addButton.getStyle().set("margin-top", "auto");
        addButton.setWidthFull();

        addButton.addClickListener(event -> {
            if (userOptional.isPresent()) {
                // User user = userOptional.get();
                // Project newProject = new Project();
                // // set owner
                // newProject.setOwner(user);
                // // set title
                // newProject.setTitle("New Card Title");
                // // set description
                // newProject.setDescription("Card Description");
                // projectsService.createNewProject(newProject);
                // ProjectCard cardLayout = new ProjectCard(newProject);
                // cardsFlexLayout.add(cardLayout);
                // // Scroll to newly added card
                // cardLayout.getElement().callJsFunction("scrollIntoView", "{ behavior: \"smooth\", block: \"end\", inline: \"nearest\" }");
            }
        });

        addProjectsGridToCardsFlexLayout(cardsFlexLayout, userOptional);

        scroller.setContent(cardsFlexLayout);

        // add the card area and then the button
        verticalLayout.add(scroller, addButton);

        // Create a responsive navbar component
        NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);
        // Add some content below the navbar
        navbar.setContent(verticalLayout);
        // Add it to the view
        add(navbar);
    }
    
    private void addProjectsGridToCardsFlexLayout(FlexLayout cardsFlexLayout, Optional<User> user) {
        if (user.isPresent()){
            // for (Project proj : projectsService.getAllByOwner(user.get().getUid())) {
            //     ProjectCard cardLayout = new ProjectCard(proj);
            //     cardsFlexLayout.add(cardLayout);
            // }
        }
        
        // Create a grid with 4 columns
        Grid<Project> grid = new Grid<>();
        grid.setSizeFull();
        grid.addColumn(Project::getName).setHeader("Name");
        grid.addColumn(Project::getCollaborationMode).setHeader("Collaboration Mode");
        grid.addColumn(Project::getAudioDetails).setHeader("Audio Details");
        grid.addColumn(Project::getStatus).setHeader("Status");
        grid.addColumn(Project::getProgress).setHeader("Progress");
        // Create a component renderer for the button column
        grid.addColumn(new ComponentRenderer<>(project -> {
        // Create a button with an eye icon
        Button button = new Button(new Icon(VaadinIcon.EYE));
        // Add a click listener to the button
        button.addClickListener(event -> {
            // Open a new page with the project details
            UI.getCurrent().getPage().open("project/" + project.getId(), "_blank");
        });
        return button;
        })).setHeader("View");

        // Get a list of projects from the mock data service
        List<Project> projects = mockDataService.getProjects();

        // Bind the data to the grid
        grid.setItems(projects);

        // Add the grid to the layout
        cardsFlexLayout.add(grid);
    }
}