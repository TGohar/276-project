package trackour.trackour.views.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import trackour.trackour.models.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

public class TopNavBar extends HorizontalLayout {

    SecurityViewService securityViewService;
    
    CustomUserDetailsService customUserDetailsService;

    public TopNavBar (
        SecurityViewService securityViewService,
        CustomUserDetailsService customUserDetailsService) {
        this.securityViewService = securityViewService;
        this.customUserDetailsService = customUserDetailsService;
        // Create a layout for the header and buttons
        setAlignItems(FlexComponent.Alignment.CENTER);
        setWidthFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H1 header = new H1("Trackour");
        expand(header);
        
        String sessionUsername = securityViewService.getAuthenticatedRequestSession().getUsername();
        // since logged in, no need to verify if this optional is empty
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();
        Text displayNameTxt = new Text(displayNameString);
        Button signUpButton = new Button("Sign Up");
        signUpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        signUpButton.addClassName("button-hover-effect");
        signUpButton.addClickListener(event -> {
            UI.getCurrent().navigate("signUp");
        });

        Button LoginButton = new Button("Logout");
        LoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        LoginButton.addClassName("button-hover-effect");
        LoginButton.addClickListener(event -> {
            securityViewService.logOut();
        });

        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.setPlaceholder("Music language");
        languageComboBox.setItems("English", "Punjabi", "Spanish", "French", "German", "Hindi");

        TextField searchField = new TextField();
        expand(searchField);
        searchField.setPlaceholder("Search Any Music");

        HorizontalLayout topNavButtons = new HorizontalLayout(displayNameTxt, LoginButton);
        topNavButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        topNavButtons.getStyle().set("gap", "10px"); // Add spacing between the buttons
        
        add(header, searchField, languageComboBox, topNavButtons);
    }
}
