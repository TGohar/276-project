package trackour.trackour.views.searchResult;
import se.michaelthelin.spotify.model_objects.specification.Track;
import trackour.trackour.spotify.SearchTrack;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.SimpleSearchField;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.yaml.snakeyaml.util.UriEncoder;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

@Route("searchResult")
@PageTitle("Search Result")

@AnonymousAllowed

public class SearchResultView extends VerticalLayout implements HasUrlParameter<String>, BeforeEnterObserver {
    
    private String search;
    // private String searchString;
    @Autowired
    SecurityViewService securityViewHandler;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    SimpleSearchField simpleSearch;
    NavBar navigation;
    AppLayout nav;
    public SearchResultView(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {
        this.securityViewHandler = securityViewHandler;
        this.customUserDetailsService = customUserDetailsService;
        simpleSearch = new SimpleSearchField();
        simpleSearch.onEnterKeyUp(event -> this.searchSubmit(event));
        this.navigation = new NavBar(customUserDetailsService, securityViewHandler);
    }

    private void generateList() {
        VerticalLayout container = new VerticalLayout();
        container.setWidthFull();


        container.add(simpleSearch);
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
        container.add(greetings);
        
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

        System.out.println("searching:"+ search);
        container.add(headingLayout);
        SearchTrack searchTracks = new SearchTrack();
        List<Track> tracks = searchTracks.getTrackList(search);
        // searchTracks.getTrack();

        System.out.println("SEARCH:" + search);
        
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

            container.add(songDiv);
        }

        navigation.setContent(container);
        nav = navigation.generateNavComponent();
        add(nav);
    
    }

    private KeyUpEvent searchSubmit(KeyUpEvent event) {
        // clear view
        this.navigation.clearContent();
        this.remove(nav);
        nav = null;
        // redirect
        getUI().ifPresent(ui -> ui.navigate(SearchResultView.class, UriEncoder.encode(simpleSearch.getSearchValue())));
        return event;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {}

    @Override
    public void setParameter(BeforeEvent event, String parametersString) {
            Location location = event.getLocation();
            // QueryParameters queryParameters = location.getQueryParameters();
            // List<String> searchQList = queryParameters.getParameters().get("search");
            // System.out.println("queryParameters.getParameters():" + queryParameters.getParameters());
            
            System.out.println("parametersString:" + parametersString);
            if (parametersString == null || parametersString.isEmpty()){
                generateList();
                return;
            }
            String searchString = URLDecoder.decode(parametersString, StandardCharsets.UTF_8);
            // System.out.println("split:" + searchString.split("?search="));
            System.out.println("searchString:" + searchString);
            this.search = searchString;
            generateList();
    }
}
