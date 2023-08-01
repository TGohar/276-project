package trackour.trackour.views.advancedsearch;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route("search/advanced")
@RouteAlias("search/advanced")
@PreserveOnRefresh
@PageTitle("Advanced Search Results for [Keyword] | Trackour")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@PermitAll
public class AdvancedSearch extends MyBlockResponsiveLayout{
    
    @Autowired
    SecurityViewService securityViewHandler;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    
    public AdvancedSearch(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {

        NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);
        
        // main container contining cards area and button
        VerticalLayout verticalLayout = new VerticalLayout();
        Div placeHolderTextContainer = new Div();
        placeHolderTextContainer.add("Advanced Search");
        verticalLayout.add(placeHolderTextContainer);
        // Create a responsive navbar component
        // Add some content below the navbar
        navbar.setContent(verticalLayout);
        // Add it to the view
        add(navbar);
    }
}
