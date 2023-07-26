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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import trackour.trackour.spotify.Playlist;

@Route(value = "Playlists")
@PageTitle("Playlists")
@AnonymousAllowed
public class PlaylistView extends Div implements HasUrlParameter<String> {
    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        Playlist p = new Playlist(parameter);
        H1 header = new H1(new Text("Discover !!"));
        add(header);
        List<PlaylistSimplified> playlists = p.getPlaylists();
        VerticalLayout playlistLayout = new VerticalLayout();
        playlistLayout.setWidth("100%");
        HorizontalLayout rowLayout = new HorizontalLayout();
        rowLayout.setWidth("100%");
        int counter = 0;
        int columns = 5;
        for (PlaylistSimplified playlist : playlists) {
            try {
                Image playListImage = new Image(playlist.getImages()[0].getUrl(), "Category Cover");
                playListImage.setWidth("200px");
                playListImage.setHeight("200px");

                // PlaylistTracksInformation trackInfo = playlist.getTracks();
                String playListID = playlist.getId();
                Button playListButton = new Button(playListImage, new ComponentEventListener<ClickEvent<Button>>() {

                    @Override
                    public void onComponentEvent(ClickEvent<Button> event) {
                        System.out.println(playListID);
                        UI.getCurrent().navigate("PlaylistItems/" + playlist.getId());
                    }
                });
                playListButton.getStyle().setWidth("200px");
                playListButton.getStyle().setHeight("200px");
                playListButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                Div playListName = new Div(new H2(new Text(playlist.getName())));
                playListName.setWidth("200px");
                VerticalLayout catLayout = new VerticalLayout();
                catLayout.add(playListButton, playListName);
                if (counter % columns == 0 && counter > 0) {
                    playlistLayout.add(rowLayout);
                    rowLayout = new HorizontalLayout();
                    rowLayout.setWidth("100%");
                }

                rowLayout.add(catLayout);
                counter++;

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
        if (rowLayout.getComponentCount() > 0) {
            playlistLayout.add(rowLayout);
        }
        add(playlistLayout);
    }
}