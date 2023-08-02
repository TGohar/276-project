package trackour.trackour.views.playlist;

import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.Playlist;
import trackour.trackour.views.components.NavBar;

@Route("Playlists/:playlistId/:playlistName")
@PageTitle("Playlist Page | Trackour")
@AnonymousAllowed
public class PlaylistView extends Div implements BeforeEnterObserver {

    private final String PLAYLIST_CARD_SIZE = "12.5rem"; // equal to 200px

    VerticalLayout contentContainer;
    private String playlistId;
    private String playlistName;

    public PlaylistView(SecurityViewService securityViewService,
            CustomUserDetailsService customUserDetailsService) {
        NavBar navbar = new NavBar(customUserDetailsService, securityViewService);
        contentContainer = new VerticalLayout();
        navbar.setContent(contentContainer);
        add(navbar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        playlistId = event.getRouteParameters().get("playlistId").get();
        playlistName = event.getRouteParameters().get("playlistName").get();
        Playlist p = new Playlist(playlistId);
        H1 header = new H1(new Text("Discover " + playlistName + " !!"));
        contentContainer.add(header);
        List<PlaylistSimplified> playlists = p.getPlaylists();

        FlexLayout playlistLayout = new FlexLayout();
        playlistLayout.setFlexGrow(1);
        playlistLayout.getStyle().set("display", "flex");
        playlistLayout.getStyle().set("flex-wrap", "wrap");
        playlistLayout.setAlignItems(FlexLayout.Alignment.CENTER);
        playlistLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);

        HorizontalLayout rowLayout = new HorizontalLayout();
        for (PlaylistSimplified playlist : playlists) {
            try {
                Image playListImage = new Image(playlist.getImages()[0].getUrl(), "Category Cover");
                playListImage.setWidth(PLAYLIST_CARD_SIZE);
                playListImage.setHeight(PLAYLIST_CARD_SIZE);

                // PlaylistTracksInformation trackInfo = playlist.getTracks();
                String playListID = playlist.getId();
                Button playListButton = new Button(playListImage, new ComponentEventListener<ClickEvent<Button>>() {

                    @Override
                    public void onComponentEvent(ClickEvent<Button> event) {
                        System.out.println(playListID);
                        UI.getCurrent().navigate("PlaylistItems/" + playlist.getId() + "/" + playlist.getName());
                    }
                });

                playListButton.getStyle().setWidth(PLAYLIST_CARD_SIZE);
                playListButton.getStyle().setHeight(PLAYLIST_CARD_SIZE);
                playListButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                Div playListName = new Div(new H2(new Text(playlist.getName())));
                playListName.setWidth(PLAYLIST_CARD_SIZE);
                VerticalLayout catLayout = new VerticalLayout();
                catLayout.add(playListButton, playListName);
                rowLayout = new HorizontalLayout();
                rowLayout.add(catLayout);
                playlistLayout.add(rowLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        contentContainer.add(playlistLayout);
    }
}
