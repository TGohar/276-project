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
import org.vaadin.klaudeta.PaginatedGrid;
import org.yaml.snakeyaml.util.UriEncoder;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
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
    PaginatedGrid<Track, Component> grid;
    VerticalLayout container;
    public SearchResultView(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {
        this.grid =  new PaginatedGrid<>();
        this.container = new VerticalLayout();
        this.securityViewHandler = securityViewHandler;
        this.customUserDetailsService = customUserDetailsService;
        simpleSearch = new SimpleSearchField();
        simpleSearch.onEnterKeyUp(event -> this.searchSubmit(event));
        this.navigation = new NavBar(customUserDetailsService, securityViewHandler);
    }

    private void generatePaginationGridLayout() {
            SearchTrack searchTracks = new SearchTrack();
            List<Track> tracks = searchTracks.getTrackList(search);
            
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            
            // TextField songField = new TextField("");
            // songField.setValue(track.getName());
            // songField.setReadOnly(true);


            HorizontalLayout trackHeader = new HorizontalLayout();
            H5 trackHeaderText = new H5("Track");
            // Center the component vertically and horizontally
            trackHeader.setAlignItems(FlexComponent.Alignment.CENTER);
            trackHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            trackHeader.add(trackHeaderText);
            grid.addColumn(new ComponentRenderer<>(track -> {
                HorizontalLayout trackCard = new HorizontalLayout();
                trackCard.setWidthFull();
                Image albumCoverImage = new Image();
                albumCoverImage.setSrc(track.getAlbum().getImages()[0].getUrl());
                albumCoverImage.setWidth("150px");
                albumCoverImage.setHeight("150px");
                albumCoverImage.addClassName("cover-image");
                albumCoverImage.getStyle().set("margin-left", "100px");

                // container for artist + track label
                VerticalLayout song_and_Artist = new VerticalLayout();
                song_and_Artist.addClassName("song-and-artist");
                song_and_Artist.getStyle().set("margin-left", "10px");
                
                // artist label
                TextField artistField = new TextField("");
                artistField.setValue(track.getArtists()[0].getName());
                artistField.setReadOnly(true);

                // song label
                TextField songField = new TextField("");
                songField.setValue(track.getName());
                songField.setReadOnly(true);

                song_and_Artist.add(artistField, songField);

                trackCard.add(albumCoverImage, song_and_Artist);

                trackCard.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
                trackCard.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
                return trackCard;
            }))
            .setHeader(trackHeader)
            .setFlexGrow(0)
            .setAutoWidth(true);


            HorizontalLayout albumHeader = new HorizontalLayout();
            H5 albumHeaderText = new H5("Album");
            // Center the component vertically and horizontally
            albumHeader.setAlignItems(FlexComponent.Alignment.CENTER);
            albumHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            albumHeader.add(albumHeaderText);
            grid.addColumn(new ComponentRenderer<>(track -> {
                HorizontalLayout albumCard = new HorizontalLayout();
                albumCard.setWidthFull();
                // Album label
                TextField albumField = new TextField("");
                albumField.setValue(track.getAlbum().getName());
                albumField.setReadOnly(true);
                albumField.getStyle().set("margin-left", "100px");
                albumCard.add(albumField);
                return albumCard;
            }))
            .setHeader(albumHeader)
            .setFlexGrow(0)
            .setAutoWidth(true);
            // grid.addColumn(Track::getAr).setHeader("Email").setSortable(true);
            // grid.addColumn(Track::getUsername).setHeader("Username").setSortable(true);
            // grid.addColumn(Track::getRoles).setHeader("Roles").setSortable(true);
            
            /**
             * 
             */
            grid.setItems(tracks);
            
            // UserGridFilter userFilter = new UserGridFilter(dataView);
            
            grid.getHeaderRows().clear(); 
            // HeaderRow headerRow = grid.appendHeaderRow();

            // headerRow.getCell(uid).setComponent(createFilterHeader("Uid", userFilter::setUid));
            // headerRow.getCell(displayName).setComponent(createFilterHeader("Display Name", userFilter::setDisplayName));
            // headerRow.getCell(email).setComponent(createFilterHeader("Email", userFilter::setEmail));
            // headerRow.getCell(username).setComponent(createFilterHeader("Username", userFilter::setUsername));
            // headerRow.getCell(roles).setComponent(createFilterHeader("Roles", userFilter::setRole));
            
            // Sets the max number of items to be rendered on the grid for each page
            grid.setPageSize(7);
            
            // Sets how many pages should be visible on the pagination before and/or after the current selected page
            grid.setPaginatorSize(3);

            grid.setPaginationLocation(PaginatedGrid.PaginationLocation.BOTTOM);
            grid.setPaginationVisibility(true);
            // gridLayout.setSizeFull();
            grid.setSizeFull();
            
            // gridLayout.add(grid);
            nav = navigation.generateNavComponent();
            container.add(simpleSearch, grid);
            navigation.setContent(container);
            add(nav);
    }

    private KeyUpEvent searchSubmit(KeyUpEvent event) {
        // clear results from old search
        clearSearchResults();
        // redirect
        getUI().ifPresent(ui -> ui.navigate(SearchResultView.class, UriEncoder.encode(simpleSearch.getSearchValue())));
        return event;
    }

    private void clearSearchResults() {
        this.navigation.clearContent();
        this.remove(nav);
        nav = null;
        container = new VerticalLayout();
        this.grid =  new PaginatedGrid<>();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {}

    @Override
    public void setParameter(BeforeEvent event, String parametersString) {
            // Location location = event.getLocation();
            // QueryParameters queryParameters = location.getQueryParameters();
            // List<String> searchQList = queryParameters.getParameters().get("search");
            // System.out.println("queryParameters.getParameters():" + queryParameters.getParameters());
            
            System.out.println("parametersString:" + parametersString);
            if (parametersString == null || parametersString.isEmpty()){
                // generateList();
                generatePaginationGridLayout();
                return;
            }
            String searchString = URLDecoder.decode(parametersString, StandardCharsets.UTF_8);
            // System.out.println("split:" + searchString.split("?search="));
            System.out.println("searchString:" + searchString);
            this.search = searchString;
            generatePaginationGridLayout();
            // generateList();
    }
}
