package trackour.trackour.views.playlist;

import java.util.List;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.PlaylistsItems;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.api.APIController;

@Route("PlaylistItems/:playlistItemId/:playlistName")
@RouteAlias("playlistitems")
@PageTitle("Playlist Items | Trackour")
@AnonymousAllowed
public class PlaylistItemsView extends Div implements BeforeEnterObserver {

    VerticalLayout contentContainer;
    private String playlistItemId;
    private String playlistName;

    public PlaylistItemsView(SecurityViewService securityViewService,
            CustomUserDetailsService customUserDetailsService) {
        NavBar navbar = new NavBar(customUserDetailsService, securityViewService);
        contentContainer = new VerticalLayout();
        navbar.setContent(contentContainer);
        add(navbar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // setText(String.format("Hello, %s!", parameter));
        playlistItemId = event.getRouteParameters().get("playlistItemId").get();
        playlistName = event.getRouteParameters().get("playlistName").get();
        PlaylistsItems items = new PlaylistsItems(playlistItemId);
        List<PlaylistTrack> playlistItem = items.getItemsInPlaylist();
        H1 header = new H1(new Text("Enjoy, " + playlistName + " !!"));
        contentContainer.add(header);
        VerticalLayout trackView = new VerticalLayout();

        if (playlistItem != null && !playlistItem.isEmpty()) {
            for (PlaylistTrack playlistTrack : playlistItem) {
                IPlaylistItem track = playlistTrack.getTrack();
                String trackName = track.getName();
                String trackURL = APIController.spotifyURL(track.getId());
                Anchor trackLink = new Anchor(trackURL, trackName);
                trackLink.setTarget("_blank");

                Image trackImage = null;
                if (track instanceof Track) {
                    Track trackItem = (Track) track;
                    // check for possible null values before accessing the array element
                    if (trackItem.getAlbum() != null && trackItem.getAlbum().getImages() != null
                            && trackItem.getAlbum().getImages().length > 0
                            && trackItem.getAlbum().getImages()[0].getUrl() != null) {
                        trackImage = new Image(trackItem.getAlbum().getImages()[0].getUrl(), "Track Image");
                        trackImage.setWidth("50px");
                        trackImage.setHeight("50px");
                    } else {
                        // handle the case when there is no image URL
                    }
                }

                Hr visuals = new Hr();
                visuals.getStyle().setWidth("100%");
                visuals.getStyle().set("margin", "0");

                FlexLayout trackLayout = new FlexLayout();
                trackLayout.getStyle().set("align-items", "center");

                if (trackImage != null) {
                    trackLayout.add(trackImage);
                }

                Div trackNameDiv = new Div(new H3(trackLink));
                trackNameDiv.getStyle().set("margin-left", "10px");
                trackNameDiv.getStyle().setWidth("100%");
                trackNameDiv.add(visuals);

                trackLayout.add(trackNameDiv);

                HorizontalLayout trackList = new HorizontalLayout();
                trackList.add(trackLayout);
                trackView.add(trackList);
            }
        } else {
            // use a common method to display an empty message
            displayEmptyMessage(trackView);
        }

        contentContainer.add(trackView);
    }

    // a common method to display an empty message
    private void displayEmptyMessage(VerticalLayout layout) {
        Div emptyListmessageDiv = new Div(new H3(new Text("This playlist is empty at the moment.")));
        emptyListmessageDiv.getStyle().set("color", "grey");
        emptyListmessageDiv.getStyle().set("text-align", "center");
        layout.add(emptyListmessageDiv);
    }
}
