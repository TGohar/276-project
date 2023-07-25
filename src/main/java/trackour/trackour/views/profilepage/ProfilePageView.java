package trackour.trackour.views.profilepage;

import java.util.Optional;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route(value = "profile")
@RouteAlias(value = "user")
@PageTitle("Profile")
@PermitAll
public class ProfilePageView extends MyBlockResponsiveLayout {

    NavBar navBar;

public ProfilePageView(CustomUserDetailsService customUserDetailsService,
                       SecurityViewService securityViewService) {
    navBar = new NavBar(customUserDetailsService, securityViewService);

    Optional<User> user = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername());

    VerticalLayout mainLayout = createMainLayout();
    VerticalLayout avatarLayout = createAvatarLayout(user.get());
    VerticalLayout infoLayout = createInfoLayout(user.get());

    mainLayout.add(avatarLayout, infoLayout);
    navBar.setContent(mainLayout);
    add(navBar);
}

private VerticalLayout createMainLayout() {
    VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setSizeFull();
    mainLayout.setPadding(false);
    mainLayout.setSpacing(false);
    // Add a background image to the main layout
    mainLayout.getStyle().set("background-image", "url('https://example.com/background.jpg')");
    mainLayout.getStyle().set("background-size", "cover");
    return mainLayout;
}

private VerticalLayout createAvatarLayout(User user) {
    VerticalLayout avatarLayout = new VerticalLayout();
    avatarLayout.setAlignItems(Alignment.CENTER);
    avatarLayout.setPadding(true);
    avatarLayout.setSpacing(true);

    Avatar avatar = new Avatar();
    avatar.setName(user.getDisplayName());
    // Add a border and a shadow to the avatar
    avatar.getStyle().set("border", "3px solid white");
    avatar.getStyle().set("box-shadow", "0 0 10px black");

    Div displayName = new Div();
    displayName.setText(user.getDisplayName() + " | @" + user.getUsername());
    displayName.addClassName("displayName");
    // Change the font size and color of the display name
    displayName.getStyle().set("font-size", "24px");
    displayName.getStyle().set("color", "white");

    avatarLayout.add(avatar, displayName);

    return avatarLayout;
}

// @Tag("paper-card")
// @NpmPackage(value = "@polymer/paper-card", version = "3.0.1")
// @NpmPackage(value = "@polymer/paper-button", version = "3.0.1")
// @JsModule("@polymer/paper-card/paper-card.js")
// @JsModule("@polymer/paper-button/paper-button.js")

private VerticalLayout createInfoLayout(User user) {
    VerticalLayout infoLayout = new VerticalLayout();
    infoLayout.setAlignItems(Alignment.START);
    infoLayout.setPadding(true);
    infoLayout.setSpacing(true);
    infoLayout.getStyle().set("border-radius", "10px");
    
    TextField displayName = new TextField("Display Name");
    displayName.setClearButtonVisible(true);
    displayName.setValue(user.getDisplayName());
    displayName.setReadOnly(true);
    
    TextField username = new TextField("Username");
    username.setClearButtonVisible(true);
    username.setValue(user.getUsername());
    username.setReadOnly(true);
    
    TextField email = new TextField("Email");
    email.setClearButtonVisible(true);
    email.setValue(user.getEmail());
    email.setReadOnly(true);
    
    PasswordField password = new PasswordField("Password");
    password.setRevealButtonVisible(false);
    password.setClearButtonVisible(true);
    password.setValue(user.getPassword());
    password.setReadOnly(true);
    
    Button editButton = new Button("Edit");
    editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    editButton.addClickListener(event -> {
      boolean isReadOnly = !email.isReadOnly();
      // Change the color of the edit button depending on the mode
      displayName.setReadOnly(isReadOnly);
      username.setReadOnly(isReadOnly);
      email.setReadOnly(isReadOnly);
      password.setReadOnly(isReadOnly);
      editButton.setText(!isReadOnly ? "Save" : "Edit");
      editButton.getStyle().set("border", "none");
      editButton.getStyle().set("box-shadow", "0 0 5px black");
      // Remove the previous variant and add the new one
      editButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
      editButton.addThemeVariants(!isReadOnly ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_PRIMARY);
    });
    
    infoLayout.add(displayName, username, email, password, editButton);
    return infoLayout;
  }
}