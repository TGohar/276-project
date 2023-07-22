package trackour.trackour.views.home;

import java.util.List;
// import java.util.Optional;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;

// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;

// import com.vaadin.addon.responsive.Responsive;
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
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.NewReleases;
import trackour.trackour.views.components.NavBar;
// import trackour.trackour.views.components.ResponsiveNavBar;
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
    // private Boolean toggleQuery;
    NavBar navBar;
    AppLayout nav;
    VerticalLayout content;
    // Integer width;
    
    public HomeView(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {
        this.content = new VerticalLayout();
        navBar = new NavBar(customUserDetailsService, securityViewHandler);
        
        initContent();
        // set the contents container
        AppLayout navAppLayout = navBar.generateNavComponent();
        navAppLayout.addToNavbar();
        navAppLayout.setContent(content);
        add(navAppLayout);
    }

    private void initContent() {
        SimpleSearchField simpleSearch = new SimpleSearchField();

        H2 newRelease = new H2("New Releases");
        newRelease.getStyle().set("margin-left", "25px");
        newRelease.getStyle().set("margin-top", "25px");
            
        NewReleases newReleases = new NewReleases();
        List<AlbumSimplified> albums = newReleases.getNewReleases();
        SimpleCarousel trendingCarousel = new SimpleCarousel(albums);
        
        H2 utiliy = new H2("Audio Utility");
        utiliy.getStyle().set("margin-left", "25px");
        
        content.add(
            simpleSearch.generateComponent(),
            newRelease,
            trendingCarousel.generateComponent(),
            utiliy
            );
        
    }
}
        /**
         * 
         thisPage.addBrowserWindowResizeListener(event -> {
             // Handle window resize event here
             Integer width = event.getWidth();
             // Integer height = event.getHeight();
 
             toggleQuery = width < 600;
 
             // if (isQueryMax) {
             //     this.isQueryTrue = this.queryWidthValue < width;
             // }
             // else {
             //     this.isQueryTrue = this.queryWidthValue > width;
             // }
 
             System.out.println("event.getWidth():" + event.getWidth());
             System.out.println("event.getHeight():" + event.getHeight());
             System.out.println("toggleQuery: " + toggleQuery);
             
 
             navBar = nav.generateNavComponent(toggleQuery);
             
         });
         */
