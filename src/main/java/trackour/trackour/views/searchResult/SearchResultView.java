package trackour.trackour.views.searchResult;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.klaudeta.PaginatedGrid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import se.michaelthelin.spotify.model_objects.specification.Track;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.SearchTrack;
import trackour.trackour.views.api.APIController;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.SimpleSearchField;
import trackour.trackour.views.login.LoginPage;

@Route("searchResult")
@RouteAlias("search")
@PageTitle("Search Result | Trackour")
@PreserveOnRefresh
@PermitAll
public class SearchResultView extends VerticalLayout implements BeforeEnterObserver {

    private String search;
    // private String searchString;
    @Autowired
    SecurityViewService securityViewHandler;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    SimpleSearchField simpleSearch;
    NavBar navigation;
    PaginatedGrid<Track, Component> grid;
    VerticalLayout container;

    public SearchResultView(SecurityViewService securityViewHandler,
            CustomUserDetailsService customUserDetailsService) {
        this.grid = new PaginatedGrid<>();
        this.container = new VerticalLayout();
        this.securityViewHandler = securityViewHandler;
        this.customUserDetailsService = customUserDetailsService;
        simpleSearch = new SimpleSearchField();
        // attach enter key listener
        simpleSearch.onEnterKeyUp(event -> {
            // get the current value of the search field
            String searchValue = simpleSearch.getSearchValue();
            // navigate to the search view with the search query as a query parameter
            getUI().ifPresent(ui -> {
                QueryParameters queryParameters = QueryParameters.simple(Map.of("query", searchValue));
                ui.getPage().setLocation("search?"+ queryParameters.getQueryString());
            });
        });
        this.navigation = new NavBar(customUserDetailsService, securityViewHandler);
    }

    private void generatePaginationGridLayout() {
        if (search != null) {
            System.out.println("search: " + search);
            SearchTrack searchTracks = new SearchTrack();
            List<Track> tracks = searchTracks.getTrackList(search);

            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

            HorizontalLayout trackHeader = new HorizontalLayout();
            // H5 trackHeaderText = new H5("Track");
            // Center the component vertically and horizontally
            trackHeader.setAlignItems(FlexComponent.Alignment.CENTER);
            trackHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            // trackHeader.add(trackHeaderText);
            grid.addColumn(new ComponentRenderer<>(track -> {
                HorizontalLayout trackCard = new HorizontalLayout();
                // trackCard.getStyle().setBackground("red");
                trackCard.setWidthFull();
                Image albumCoverImage = new Image();
                albumCoverImage.setSrc(track.getAlbum().getImages()[0].getUrl());
                albumCoverImage.setWidth("150px");
                albumCoverImage.setHeight("150px");
                albumCoverImage.addClassName("cover-image");
                albumCoverImage.getStyle().set("margin-left", "100px");

                // container for artist + track label
                VerticalLayout artist_and_Album = new VerticalLayout();
                artist_and_Album.addClassName("song-and-artist");
                artist_and_Album.getStyle().set("margin-left", "10px");

                // artist label
                H5 aristLabel = new H5("Artist: ");
                TextField artistField = new TextField("");
                artistField.setValue(track.getArtists()[0].getName());
                artistField.setReadOnly(true);
                aristLabel.add(artistField);

                // song label
                TextField songField = new TextField("");
                songField.setValue(track.getName());
                songField.setReadOnly(true);

                // Album label
                H5 albumLabel = new H5("Album: ");
                TextField albumField = new TextField("");
                albumField.setValue(track.getAlbum().getName());
                albumField.setReadOnly(true);
                albumLabel.add(albumField);

                
                
                Button infoButton = new Button("Show Audio Features");
                // infoButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                
                
                infoButton.addClickListener(e -> {
                    
                    String trackID = track.getId();
        
                    float acousticness = APIController.getAcousticness(trackID);
                    float danceability = APIController.getDanceability(trackID);
                    float energy = APIController.getEnergy(trackID);
                    float instrumentalness = APIController.getInstrumentalness(trackID);
                    float liveness = APIController.getLiveness(trackID);
                    float valence = APIController.getValence(trackID);
                    float tempo = APIController.getTempo(trackID);
                    int timeSignature = APIController.getTimeSignature(trackID);
                    float loudness = APIController.getLoudness(trackID);
                    float key = APIController.getKey(trackID);
                    float mode = APIController.getMode(trackID);

                    Image trackImage = new Image();
                    trackImage.setSrc(track.getAlbum().getImages()[0].getUrl());
                    trackImage.setWidth("150px");
                    trackImage.setHeight("150px");

                        
                    Dialog dialog = new Dialog();
                    dialog.setWidth("700px");
                    dialog.setHeight("800px");
                    dialog.setCloseOnEsc(true);
                    dialog.setCloseOnOutsideClick(true);
                    dialog.setModal(true);
                    dialog.setDraggable(true);
                    dialog.setResizable(true);
                    // dialog.getStyle().setOpacity("0");


                    VerticalLayout dialogLayout = new VerticalLayout();
                    dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                    dialogLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                    dialogLayout.getStyle().set("margin-top", "15px");
                    dialogLayout.getStyle().set("margin-left", "25px");
                    dialogLayout.getStyle().set("margin-right", "100px");
                    dialogLayout.getStyle().set("margin-bottom", "50px");
                    dialogLayout.getStyle().set("background-color", "#C9CCD5F"); //7195C3
                    dialogLayout.getStyle().set("border-radius", "25px");
                    dialogLayout.getStyle().set("box-shadow", "0 0 20px #000000");
                    dialogLayout.getStyle().set("color", "#FFFFFF");
                    dialogLayout.getStyle().set("font-size", "20px");
                    dialogLayout.getStyle().set("font-weight", "bold");
                    dialogLayout.getStyle().set("text-align", "center");
                    dialogLayout.getStyle().set("width", "600px");
                    dialogLayout.getStyle().set("height", "900px");


                    HorizontalLayout dialogHeader = new HorizontalLayout();
                    dialogHeader.add(new H5("Song: " + track.getName()));
                    dialogHeader.add(new H5("Artist: " + track.getArtists()[0].getName()));
                    dialogHeader.add(new H5("Album: " + track.getAlbum().getName()));
                    dialogHeader.setAlignItems(FlexComponent.Alignment.CENTER);
                    dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                    dialogHeader.getStyle().set("color", "#4B6587");
                    dialogHeader.getStyle().set("margin-bottom", "40px");
                    
                    dialogLayout.add(trackImage);
                    dialogLayout.add(dialogHeader);
                    dialogLayout.add(new H3("Audio Features"));
                    dialogLayout.add(new H5("Acousticness: " + acousticness));
                    dialogLayout.add(new H5("Danceability: " + danceability));
                    dialogLayout.add(new H5("Energy: " + energy));
                    dialogLayout.add(new H5("Instrumentalness: " + instrumentalness));
                    dialogLayout.add(new H5("Liveness: " + liveness));
                    dialogLayout.add(new H5("Valence: " + valence));
                    dialogLayout.add(new H5("Tempo: " + tempo));
                    dialogLayout.add(new H5("Time Signature: " + timeSignature));
                    dialogLayout.add(new H5("Loudness: " + loudness));
                    dialogLayout.add(new H5("Key: " + key));
                    dialogLayout.add(new H5("Mode: " + mode));
                    dialogLayout.add(new H5("Popularity: " + track.getPopularity()));
                    dialogLayout.add(new H5("Duration: " + track.getDurationMs() + " ms"));

                    dialog.add(dialogLayout);
                    dialog.open();
                });


                // play button
                String trackURL = APIController.spotifyURL(track.getId());
                Anchor trackLink = new Anchor(trackURL, new Icon(VaadinIcon.PLAY_CIRCLE));
                trackLink.setTarget("_blank");

                Button playButton = new Button(trackLink);
                playButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                artist_and_Album.add(aristLabel, albumLabel);

                trackCard.add(albumCoverImage, songField, artist_and_Album, infoButton ,playButton);

                trackCard.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
                trackCard.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
                return trackCard;
            }))
                    // .setHeader(trackHeader)
                    .setFlexGrow(1)
                    .setAutoWidth(true);

            grid.setItems(tracks);

            // Sets the max number of items to be rendered on the grid for each page
            grid.setPageSize(7);

            // Sets how many pages should be visible on the pagination before and/or after
            // the current selected page
            grid.setPaginatorSize(3);

            grid.setPaginationLocation(PaginatedGrid.PaginationLocation.BOTTOM);
            grid.setPaginationVisibility(true);
            // gridLayout.setSizeFull();
            grid.setSizeFull();

            // gridLayout.add(grid);
            // generate responsive navbar
            navigation = new NavBar(customUserDetailsService, securityViewHandler);
            container.add(simpleSearch, grid);
            navigation.setContent(container);
            add(navigation);
        } else {
            navigation = new NavBar(customUserDetailsService, securityViewHandler);
            container.add(simpleSearch);
            navigation.setContent(container);
            add(navigation);
        }

    }

    //    private void trackFeaturesDialog(float acousticness,float danceability,float energy,float instrumentalness,float liveness,float valence,float tempo,float timeSignature,float loudness,float key,float mode)
    // {
    //     Dialog dialog = new Dialog();
    //     dialog.setWidth("500px");
    //     dialog.setHeight("500px");
    //     dialog.setCloseOnEsc(true);
    //     dialog.setCloseOnOutsideClick(true);

    //     VerticalLayout dialogLayout = new VerticalLayout();
    //     dialogLayout.setPadding(true);
    //     dialogLayout.setSpacing(true);
    //     dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

    //     H3 dialogTitle = new H3("Audio Features");
    //     dialogLayout.add(dialogTitle);

    //     dialogLayout.add("Acousticness: " + acousticness);
    //     dialogLayout.add("Danceability: " + danceability);
    //     dialogLayout.add("Energy: " + energy);
    //     dialogLayout.add("Instrumentalness: " + instrumentalness);
    //     dialogLayout.add("Liveness: " + liveness);
    //     dialogLayout.add("Valence: " + valence);
    //     dialogLayout.add("Tempo: " + tempo);
    //     dialogLayout.add("Time Signature: " + timeSignature);
    //     dialogLayout.add("Loudness: " + loudness);
    //     dialogLayout.add("Key: " + key);
    //     dialogLayout.add("Mode: " + mode);

    //     dialog.add(dialogLayout);
    //     dialog.open();

    // }

    private void clearPage() {
        this.navigation.clearContent();
        this.remove(navigation);
        navigation = null;
        container = new VerticalLayout();
        this.grid = new PaginatedGrid<>();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clearPage();
        // check if the user is authenticated
        if (securityViewHandler.getSessionOptional().isPresent()) {
            // get url param
            QueryParameters queryParameters = event.getLocation().getQueryParameters();
            Map<String, List<String>> parametersMap = queryParameters.getParameters();
            if (parametersMap.containsKey("query")) {
                this.search = parametersMap.get("query").get(0);
            } else {
                this.search = null;
            }
            // update view with the search res
            generatePaginationGridLayout();
        } else {
            // the user is not authenticated, redirect to the login view
            event.rerouteTo(LoginPage.class);
        }
    }

    
}
