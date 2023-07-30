package trackour.trackour.views.advancedsearch;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route("search/advanced")
@RouteAlias("search/advanced")
@PreserveOnRefresh
@PageTitle("Advanced Search Results for [Keyword] | Trackour")
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

        String pattern = "^((0\\\\.[0-9]{1})|(1\\\\.0))$";

        NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);
        
        // main container contining cards area and button
        VerticalLayout verticalLayout = new VerticalLayout();
        Div placeHolderTextContainer = new Div();
        placeHolderTextContainer.add("Advanced Search");
        verticalLayout.add(placeHolderTextContainer);
        // Create a responsive navbar component
        // Add some content below the navbar
        navbar.setContent(verticalLayout);

        TextField acousticness = new TextField();
        acousticness.setLabel("Acousticness");
        acousticness.setHelperText("The acousticness of the track as a value between 0.0 and 1.0." +
                                    " The higher the value, the higher the chance the track is acoustic.");
        acousticness.setPattern(pattern);
        acousticness.setRequired(true);
        
        TextField danceability = new TextField();
        danceability.setLabel("Danceability");
        danceability.setHelperText("The danceability of the track as a value between 0.0 and 1.0." +
                                    " The danceability depends on factors like tempo and rhythm stability. Higher is better.");
        danceability.setPattern(pattern);
        danceability.setRequired(true);

        TextField energy = new TextField();
        energy.setLabel("Energy");
        energy.setHelperText("The energy of the track as a value between 0.0 and 1.0. " +
                            " The energetic value of the track depends on factors like speed and loudness." +
                            " Fast and loud tracks feel more energetic than slow and quiet tracks.");
        energy.setPattern(pattern);
        energy.setRequired(true);

        TextField instrumentalness = new TextField();
        instrumentalness.setLabel("Instrumentalness");
        instrumentalness.setHelperText("Get the instrumentalness of the track as a value between 0.0 and 1.0" +
                                        " The higher the value, the higher the chance the track contains vocals.");
        instrumentalness.setPattern(pattern);
        instrumentalness.setRequired(true);

        ComboBox<String> key = new ComboBox<>();
        key.setLabel("Key");
        key.setAllowCustomValue(false);
        key.setItems("C", "C♯/D♭", "D", "D♯/E♭", "E", "F", "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B");
        key.setHelperText("The key the track is in.");
        key.setRequired(true);

        TextField loudness = new TextField();
        loudness.setLabel("Loudness");
        loudness.setHelperText("The average loudness of the track." + 
                                " These values have mostly a range between -60 and 0 decibels.");
        loudness.setRequired(true);

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

        TextField timeSignature = new TextField();
        timeSignature.setLabel("Time Signature");
        timeSignature.setHelperText("The estimated overall time signature of the track." +
                                    " The time signature is the number of beats in a bar." +
                                    " A three quarters beat would be given as the value 3.");
        timeSignature.setPattern("[2-8]");
        timeSignature.setRequired(true);

        TextField valence = new TextField();
        valence.setLabel("Valence");
        valence.setHelperText("The valence of the track as a value between 0.0 and 1.0." +
                                " A track with high valence sounds more positive (happy, cheerful, euphoric)" +
                                " than a track with a low valence value (sad, depressed, angry).");
        valence.setPattern(pattern);
        valence.setRequired(true);

        VerticalLayout test = new VerticalLayout();
        Dialog warning = new Dialog();
        warning.setHeaderTitle("Warning!");

        warning.add(test);
        
        Button searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(acousticness, danceability, energy, instrumentalness, key, 
                        loudness, mode, tempo, timeSignature, valence, searchButton);
        formLayout.setResponsiveSteps(
            new ResponsiveStep("0", 1),
            new ResponsiveStep("300px", 3)
        );
        formLayout.setHeight("400px");
        formLayout.setWidth("600px");;
        
        // Add it to the view
        add(navbar, formLayout, warning);
    }
}
