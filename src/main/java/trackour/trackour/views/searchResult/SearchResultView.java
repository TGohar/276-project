package trackour.trackour.views.searchResult;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.klaudeta.PaginatedGrid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridVariant;
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

                artist_and_Album.add(aristLabel, albumLabel);

                trackCard.add(albumCoverImage, songField, artist_and_Album);

                trackCard.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
                trackCard.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
                return trackCard;
            }))
                    // .setHeader(trackHeader)
                    .setFlexGrow(1)
                    .setAutoWidth(true);

            grid.addColumn(new ComponentRenderer<>(track -> {
                // vaadin:arrow-forward
                Button moreInfoButton = new Button(new Icon(VaadinIcon.INFO_CIRCLE_O));
                moreInfoButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                moreInfoButton.setAriaLabel("More Info");
                moreInfoButton.setTooltipText("View more info about this track in a new tab");
                return moreInfoButton;
            }))
                    .setFlexGrow(0)
                    .setAutoWidth(true);

            // when user clicks on list item, the playback feature shows
            grid.setItemDetailsRenderer(new ComponentRenderer<>(track -> {
                Button playbackButton = new Button(new Icon(VaadinIcon.PLAY_CIRCLE_O));
                playbackButton.addThemeVariants(ButtonVariant.LUMO_ICON);
                playbackButton.setAriaLabel("Listen to song");
                playbackButton.setTooltipText("Playback feature");
                return playbackButton;
            }));

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
