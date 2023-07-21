package trackour.trackour.views.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

/**Change the color of a div is the window size < 1024px and div width < 300px **/
@Route("mediaquery")
@RolesAllowed({"ADMIN", "USER"})
public class MediaQueryView extends VerticalLayout {

    @Autowired
    SecurityViewService securityViewHandler;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    Boolean isMobileView;

    public MediaQueryView(SecurityViewService securityViewHandler,
            CustomUserDetailsService customUserDetailsService) {
            this.isMobileView = true;
            NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);
            add(navbar.generateNavComponent(true));
    }
}