package trackour.trackour.views.home;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.models.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.TopNavBar;

@Route("")
// Admins are users but also have the "admin" special role so pages that can be viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({"ADMIN", "USER"})
public class HomeView extends VerticalLayout {

    @Autowired
    SecurityViewService securityViewService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    TopNavBar topNavBar;

    public HomeView(
        SecurityViewService securityViewService,
        CustomUserDetailsService customUserDetailsService) {
        this.securityViewService = securityViewService;
        this.customUserDetailsService = customUserDetailsService;
        this.topNavBar = new TopNavBar(securityViewService, customUserDetailsService);

        add(topNavBar);
    }
}
