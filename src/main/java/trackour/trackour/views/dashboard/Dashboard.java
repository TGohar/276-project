package trackour.trackour.views.dashboard;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.Project;
import trackour.trackour.model.ProjectsService;
import trackour.trackour.model.User;
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
    
    public Dashboard(
        SecurityViewService securityViewHandler,
        CustomUserDetailsService customUserDetailsService,
        ProjectsService projectsService) {
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
                User user = userOptional.get();
                Project newProject = new Project();
                // set owner
                newProject.setOwner(user.getUid());
                // set title
                newProject.setTitle("New Card Title");
                // set description
                newProject.setDescription("Card Description");
                projectsService.createNewProject(newProject);
                ProjectCard cardLayout = new ProjectCard(newProject);
                cardsFlexLayout.add(cardLayout);
                // Scroll to newly added card
                cardLayout.getElement().callJsFunction("scrollIntoView", "{ behavior: \"smooth\", block: \"end\", inline: \"nearest\" }");
            }
        });

        addCardsToCardsFlexLayout(cardsFlexLayout, userOptional);

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
    
    private void addCardsToCardsFlexLayout(FlexLayout cardsFlexLayout, Optional<User> user) {
        if (user.isPresent()){
            for (Project proj : projectsService.getAllByOwner(user.get().getUid())) {
                ProjectCard cardLayout = new ProjectCard(proj);
                cardsFlexLayout.add(cardLayout);
            }
        }
    }
}