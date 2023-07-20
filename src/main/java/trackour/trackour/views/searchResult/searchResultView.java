package trackour.trackour.views.searchResult;
import se.michaelthelin.spotify.model_objects.specification.Track;
import trackour.trackour.spotify.SearchTrack;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.SimpleSearchFiield;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

@Route("searchResult")
@PageTitle("Search Result")

@AnonymousAllowed

public class SearchResultView extends VerticalLayout {
    public SearchResultView(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {


        NavBar navigation = new NavBar(customUserDetailsService, securityViewHandler);
        add(navigation.generateComponent());

        SimpleSearchFiield simpleSearch = new SimpleSearchFiield();
        add(simpleSearch.generateComponent());

        SearchTrack searchTracks = new SearchTrack();
        Optional<UserDetails> username = securityViewHandler.getSessionOptional();
        String sessionUsername = username.get().getUsername();
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();

        Icon smile = new Icon(VaadinIcon.SMILEY_O);
        smile.setColor("Pink");
        H1 header = new H1("Hi "+displayNameString + ", here is the joy your ears have been waiting for!");
        HorizontalLayout greetings = new HorizontalLayout();

        greetings.setAlignItems(FlexComponent.Alignment.START);
        greetings.add(header);
        greetings.getStyle().set("align-items", "center");
        greetings.getStyle().set("margin-bottom", "100px");
        add(greetings);
        
        H2 trackHeading = new H2("Track");
        H2 albumHeading = new H2("Album");
        H2 durationHeading = new H2("Duration");

        trackHeading.getStyle().set("margin-left", "250px");
        albumHeading.getStyle().set("margin-left", "320px");
        durationHeading.getStyle().set("margin-left", "190px");
        
        HorizontalLayout headingLayout = new HorizontalLayout();
        headingLayout.add(trackHeading, albumHeading, durationHeading);
        headingLayout.getStyle().set("align-items", "center");
        headingLayout.getStyle().set("margin-bottom", "50px");

        add(headingLayout);
        
        List<Track> tracks = searchTracks.getTrack();
        
         for (Track track : tracks) {
            long durationMin = track.getDurationMs()/60000;
            long durationSec = (track.getDurationMs()%60000)/1000;
            TextField trackTime = new TextField("");
            trackTime.setValue(String.valueOf(durationMin) + ":" + String.valueOf(durationSec));
            trackTime.setReadOnly(true);
            trackTime.getStyle().set("margin-left", "100px");

            TextField artistField = new TextField("");
            artistField.setValue(track.getArtists()[0].getName());
            artistField.setReadOnly(true);

            TextField songField = new TextField("");
            songField.setValue(track.getName());
            songField.setReadOnly(true);
        

            TextField albumField = new TextField("");
            albumField.setValue(track.getAlbum().getName());
            albumField.setReadOnly(true);
            albumField.getStyle().set("margin-left", "100px");

            Image albumCoverImage = new Image();
            albumCoverImage.setSrc(track.getAlbum().getImages()[0].getUrl());
            albumCoverImage.setWidth("150px");
            albumCoverImage.setHeight("150px");
            albumCoverImage.addClassName("cover-image");
            albumCoverImage.getStyle().set("margin-left", "100px");

            VerticalLayout song_and_Artist = new VerticalLayout();
            song_and_Artist.add(artistField, songField);
            song_and_Artist.addClassName("song-and-artist");
            song_and_Artist.getStyle().set("margin-left", "10px");

            HorizontalLayout trackInfo = new HorizontalLayout();
            trackInfo.add(albumCoverImage, song_and_Artist, albumField, trackTime);
            trackInfo.getStyle().set("align-items", "center");

            Div songDiv = new Div(trackInfo);
            songDiv.getStyle().set("align-items", "center");
            
            songDiv.addClassName("song-div");

            add(songDiv);
        }
    }
}
