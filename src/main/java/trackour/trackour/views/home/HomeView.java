package trackour.trackour.views.home;

import java.util.List;
// import java.util.Optional;

// import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.Text;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.NewReleases;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.SimpleSearchFiield;

@Route("")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({ "ADMIN", "USER" })
public class HomeView extends VerticalLayout {
    MenuBar mobileVMenuBar;
    Component mobileView;

    public HomeView(SecurityViewService securityViewHandler,
            CustomUserDetailsService customUserDetailsService) {
        NavBar nav = new NavBar(customUserDetailsService, securityViewHandler);
        SimpleSearchFiield simpleSearch = new SimpleSearchFiield();
        add(
            nav.generateComponent(),
            simpleSearch.generateComponent()
        );

        H2 newRelease = new H2("New Releases");
        newRelease.getStyle().set("margin-left", "25px");
        newRelease.getStyle().set("margin-top", "25px");
        HorizontalLayout tLayout = new HorizontalLayout();
        NewReleases newReleases = new NewReleases();
        List<AlbumSimplified> albums = newReleases.getNewReleases();
        Scroller trendinScroller = new Scroller();

        // set the width of the scroller area to 100% to not overflow over the side of the page
        trendinScroller.setWidthFull();
        trendinScroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        try {
            for (AlbumSimplified album : albums) {
            Image coverImage = new Image(album.getImages()[0].getUrl(), "Album Cover");
            coverImage.setWidth("200px");
            coverImage.setHeight("200px");
            Icon playIcon = new Icon(VaadinIcon.PLAY_CIRCLE);
            playIcon.setColor("Green");
            playIcon.setClassName("playicon");

            Button albumButton = new Button(coverImage);
            // albumButton.setIcon(playIcon);
            albumButton.getStyle().setWidth("200px");
            albumButton.getStyle().setHeight("200px");
            albumButton.addClassName("albumB");
            albumButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Div albumInfo = new Div(new Text(album.getName()));
            albumInfo.setWidth("200px");

            VerticalLayout albumLayout = new VerticalLayout();
            albumLayout.add(albumButton, albumInfo);

            tLayout.add(albumLayout);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        trendinScroller.setContent(tLayout);
        add(newRelease, trendinScroller);

        H2 utiliy = new H2("Audio Utility");
        utiliy.getStyle().set("margin-left", "25px");
        add(utiliy);
    }
}
