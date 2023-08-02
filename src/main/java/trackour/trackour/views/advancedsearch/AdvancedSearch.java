package trackour.trackour.views.advancedsearch;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.api.APIController;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route("search/advanced")
@RouteAlias("search/advanced")
@PreserveOnRefresh
@PageTitle("Advanced Search | Trackour")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@PermitAll
public class AdvancedSearch extends MyBlockResponsiveLayout{
    
    @Autowired
    SecurityViewService securityViewHandler;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    
    public AdvancedSearch(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {

        NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);

        TextField genre = new TextField();
        genre.setLabel("Genre");
        genre.setHelperText("The genre you want to search in.");
        genre.setRequired(true);
        genre.setAllowedCharPattern("[a-z,]");

        TextField acousticness = new TextField();
        acousticness.setLabel("Acousticness");
        acousticness.setHelperText("The acousticness of the track as a value between 0.0 and 1.0." +
                                    " The higher the value, the higher the chance the track is acoustic.");
        acousticness.setRequired(true);
        acousticness.setAllowedCharPattern("[0-9.]");
        
        TextField danceability = new TextField();
        danceability.setLabel("Danceability");
        danceability.setHelperText("The danceability of the track as a value between 0.0 and 1.0." +
                                    " The danceability depends on factors like tempo and rhythm stability. Higher is better.");
        danceability.setRequired(true);
        danceability.setAllowedCharPattern("[0-9.]");

        TextField energy = new TextField();
        energy.setLabel("Energy");
        energy.setHelperText("The energy of the track as a value between 0.0 and 1.0. " +
                            " The energetic value of the track depends on factors like speed and loudness." +
                            " Fast and loud tracks feel more energetic than slow and quiet tracks.");
        energy.setRequired(true);
        energy.setAllowedCharPattern("[0-9.]");

        TextField instrumentalness = new TextField();
        instrumentalness.setLabel("Instrumentalness");
        instrumentalness.setHelperText("Get the instrumentalness of the track as a value between 0.0 and 1.0" +
                                        " The higher the value, the higher the chance the track contains vocals.");
        instrumentalness.setRequired(true);
        instrumentalness.setAllowedCharPattern("[0-9.]");

        ComboBox<String> key = new ComboBox<>();
        key.setLabel("Key");
        key.setAllowCustomValue(false);
        key.setItems("C", "C♯/D♭", "D", "D♯/E♭", "E", "F", "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B");
        key.setHelperText("The key the track is in.");
        key.setRequired(true);

        TextField loudness = new TextField();
        loudness.setLabel("Loudness");
        loudness.setHelperText("The average loudness of the track." + 
                                " These values have mostly a range between -60 and 0 decibels." +
                                " Input values as positive.");
        loudness.setRequired(true);
        loudness.setAllowedCharPattern("[0-9.]");

        ComboBox<String> mode = new ComboBox<>();
        mode.setLabel("Mode");
        mode.setAllowCustomValue(false);
        mode.setItems("Major", "Minor");
        mode.setHelperText("Mode indicates the modality of a track," + 
                            " the type of scale from which its melodic content is derived.");
        mode.setRequired(true);  

        TextField tempo = new TextField();
        tempo.setLabel("Tempo");
        tempo.setHelperText("The estimated tempo of the track in beats per minute." +
                            " Tempo is the speed or pace of a given piece, derived from the average beats duration.");
        tempo.setRequired(true);
        tempo.setAllowedCharPattern("[0-9.]");

        TextField timeSignature = new TextField();
        timeSignature.setLabel("Time Signature");
        timeSignature.setHelperText("The estimated overall time signature of the track." +
                                    " The time signature is the number of beats in a bar." +
                                    " A three quarters beat would be given as the value 3." +
                                    " Range Allowed: 3-7");
        timeSignature.setAllowedCharPattern("[3-7]");
        timeSignature.setRequired(true);

        TextField valence = new TextField();
        valence.setLabel("Valence");
        valence.setHelperText("The valence of the track as a value between 0.0 and 1.0." +
                                " A track with high valence sounds more positive (happy, cheerful, euphoric)" +
                                " than a track with a low valence value (sad, depressed, angry).");
        valence.setRequired(true);
        valence.setAllowedCharPattern("[0-9.]");

        Dialog errorMessage = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        errorMessage.add(dialogLayout);
        errorMessage.setHeaderTitle("ERROR");
        
        Paragraph errorParagraph = new Paragraph();
        errorParagraph.setText("Input is invalid!");
        dialogLayout.add(errorParagraph);

        Button closeButton = new Button(new Icon("lumo", "cross"),
            (e) -> errorMessage.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        errorMessage.getHeader().add(closeButton);

        Button searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(event -> {
            
            if (Float.parseFloat(acousticness.getValue()) > 1.0) {
                errorMessage.open();
                acousticness.setInvalid(true);
            }

            if (Float.parseFloat(danceability.getValue()) > 1.0) {
                errorMessage.open();
                danceability.setInvalid(true);
            }

            if (Float.parseFloat(energy.getValue()) > 1.0) {
                errorMessage.open();
                energy.setInvalid(true);
            }

            if (Float.parseFloat(instrumentalness.getValue()) > 1.0) {
                errorMessage.open();
                instrumentalness.setInvalid(true);
            }

            if (Float.parseFloat(loudness.getValue()) > 60) {
                errorMessage.open();
                loudness.setInvalid(true);
            }

            if (Float.parseFloat(tempo.getValue()) > 200) {
                errorMessage.open();
                tempo.setInvalid(true);
            }

            if (Integer.parseInt(timeSignature.getValue()) > 7) {
                errorMessage.open();
                timeSignature.setInvalid(true);
            }

            if (Float.parseFloat(valence.getValue()) > 1.0) {
                errorMessage.open();
                valence.setInvalid(true);
            }

            String genreValue = genre.getValue().toLowerCase();
            float acousticnessValue = Float.parseFloat(acousticness.getValue());
            float danceabilityValue = Float.parseFloat(danceability.getValue());
            float energyValue = Float.parseFloat(energy.getValue());
            float instrumentalnessValue = Float.parseFloat(instrumentalness.getValue());
            int keyValue;
            String keyLetter = key.getValue();
            switch (keyLetter) {
                case "C": keyValue = 0;
                    break;
                case "C♯/D♭": keyValue = 1;
                    break;
                case "D": keyValue = 2;
                    break;
                case "D♯/E♭": keyValue = 3;
                    break;
                case "E": keyValue = 4;
                    break;
                case "F": keyValue = 5;
                    break;
                case "F♯/G♭": keyValue = 6;
                    break;
                case "G": keyValue = 7;
                    break;
                case "G♯/A♭": keyValue = 8;
                    break;
                case "A": keyValue = 9;
                    break;
                case "A♯/B♭": keyValue = 10;
                    break;
                case "B": keyValue = 11;
                    break;
                default: keyValue = -1;
                    break;
            }

            float loudnessValue = -Float.parseFloat(loudness.getValue());
            int modeValue;
            if (mode.getValue() == "Major") {
                modeValue = 1;
            } else {
                modeValue = 0;
            }
            float tempoValue = Float.parseFloat(tempo.getValue());
            int timeSignatureValue = Integer.parseInt(timeSignature.getValue());
            float valenceValue = Float.parseFloat(valence.getValue());

            TrackSimplified[] tracks = APIController.getRecommendations(genreValue, acousticnessValue, danceabilityValue, energyValue, instrumentalnessValue, keyValue, loudnessValue, modeValue, tempoValue, timeSignatureValue, valenceValue);

            // TESTING
            System.out.println(tracks[0].getName());
        });
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(genre, acousticness, danceability, energy, instrumentalness, key, 
                        loudness, mode, tempo, timeSignature, valence, searchButton);
        formLayout.setResponsiveSteps(
            new ResponsiveStep("0", 1),
            new ResponsiveStep("300px", 3)
        );
        formLayout.setHeight("400px");
        formLayout.setWidth("900px");

        Grid<TrackSimplified> resultsGrid = new Grid<>();
        resultsGrid.addColumn(TrackSimplified::getName).setHeader("Song");
        resultsGrid.addColumn(TrackSimplified::getArtists).setHeader("Artist");
        resultsGrid.setHeight("400px");
        resultsGrid.setWidth("900px");

        // main container contining cards area and button
        VerticalLayout verticalLayout = new VerticalLayout();
        Div placeHolderTextContainer = new Div();
        placeHolderTextContainer.add(new H3("Advanced Search"));

        HorizontalLayout main = new HorizontalLayout();
        main.add(formLayout, resultsGrid);

        verticalLayout.add(placeHolderTextContainer, main);
        verticalLayout.setAlignItems(Alignment.CENTER);
        // Create a responsive navbar component
        // Add some content below the navbar
        navbar.setContent(verticalLayout);

        setAlignItems(Alignment.CENTER);
        // Add it to the view
        add(navbar);
    }
}
