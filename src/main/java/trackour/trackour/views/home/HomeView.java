package trackour.trackour.views.home;

import java.util.List;
// import java.util.Optional;
// import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
//import com.vaadin.flow.component.Text;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.menubar.MenuBar;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.NewReleases;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.SimpleCarousel;
import trackour.trackour.views.components.SimpleSearchField;

@Route("")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({ "ADMIN", "USER" })
public class HomeView extends VerticalLayout {
    MenuBar mobileVMenuBar;
    Component mobileView;

    @Autowired
    SecurityViewService securityViewHandler;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    public HomeView(SecurityViewService securityViewHandler,
            CustomUserDetailsService customUserDetailsService) {
        NavBar nav = new NavBar(customUserDetailsService, securityViewHandler);
        AppLayout navBar = nav.generateNavComponent(false);
        SimpleSearchField simpleSearch = new SimpleSearchField();

        H2 newRelease = new H2("New Releases");
        newRelease.getStyle().set("margin-left", "25px");
        newRelease.getStyle().set("margin-top", "25px");
        
        NewReleases newReleases = new NewReleases();
        List<AlbumSimplified> albums = newReleases.getNewReleases();
        SimpleCarousel trendingCarousel = new SimpleCarousel(albums);
        
        H2 utiliy = new H2("Audio Utility");
        utiliy.getStyle().set("margin-left", "25px");
        VerticalLayout content = new VerticalLayout();
        // content.getStyle().setBackground("lime");

        // set the contents
        content.add(
            simpleSearch.generateComponent(),
            newRelease,
            trendingCarousel.generateComponent(),
            utiliy
        );
        // set the contents container
        navBar.setContent(content);
        // add the nav component to this view
        add(navBar);
    }
}
