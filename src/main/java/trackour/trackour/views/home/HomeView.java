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
        // H1 header = new H1("Trackour");

        // Optional<UserDetails> username = securityViewHandler.getRequestSession();
        // String sessionUsername = username.get().getUsername();
        // String displayNameString =
        // customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();
        // Text displayNameTxt = new Text("@" + displayNameString);
        // Button signUpButton = new Button("Sign Up");
        // signUpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // signUpButton.addClassName("button-hover-effect");
        // signUpButton.addClickListener(event -> {
        // UI.getCurrent().navigate("signUp");
        // });

        // Button LoginButton = new Button("Logout");
        // LoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // LoginButton.addClassName("button-hover-effect");
        // LoginButton.addClickListener(event -> {
        // securityViewHandler.logOut();
        // });

        // ComboBox<String> languageComboBox = new ComboBox<>();
        // languageComboBox.setPlaceholder("Music language");
        // languageComboBox.setItems("English", "Punjabi", "Spanish", "French",
        // "German", "Hindi");

        // TextField searchField = new TextField();
        // searchField.setPlaceholder("Search Music");
        // searchField.setPrefixComponent(new Icon("lumo", "search"));

        // Button mediaShelfButton = new Button("Media Shelf", new
        // Icon(VaadinIcon.MUSIC));

        // HorizontalLayout topNavButtons = new HorizontalLayout(exploreButton,
        // searchField, languageComboBox,
        // mediaShelfButton, displayNameTxt, LoginButton);
        // topNavButtons.addClassName("topNavButtons");
        // topNavButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // topNavButtons.setWidthFull();

        // HorizontalLayout topNavBar = new HorizontalLayout(header, topNavButtons);

        // topNavBar.setAlignItems(FlexComponent.Alignment.CENTER);
        // topNavBar.setWidthFull();
        // topNavBar.expand(searchField);
        // topNavBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        
        NavBar obj = new NavBar(customUserDetailsService, securityViewHandler);
        add(obj.generateNavBar());

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
