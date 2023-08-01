package trackour.trackour.views.profilepage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.Project;
import trackour.trackour.model.ProjectsService;
import trackour.trackour.model.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route(value = "profile")
@RouteAlias(value = "user")
@PageTitle("Profile")
@PermitAll
public class ProfilePageView extends MyBlockResponsiveLayout {

private final String emailValidationRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
  + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";;

NavBar navBar;
CustomUserDetailsService customUserDetailsService;
SecurityViewService securityViewService;
ProjectsService projectsService;
User user;

public ProfilePageView(CustomUserDetailsService customUserDetailsService, ProjectsService projectsService,
                       SecurityViewService securityViewService) {
    navBar = new NavBar(customUserDetailsService, securityViewService);

    this.customUserDetailsService = customUserDetailsService;
    this.projectsService = projectsService;
    this.securityViewService = securityViewService;

    this.user = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername()).get();



    VerticalLayout mainLayout = createMainLayout();
    VerticalLayout avatarLayout = createAvatarLayout();
    HorizontalLayout menuLayout = createMenuLayout();

    mainLayout.add(avatarLayout, menuLayout);
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

private VerticalLayout createAvatarLayout() {
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

private HorizontalLayout createMenuLayout() {
  HorizontalLayout menuLayout = new HorizontalLayout();
  menuLayout.setSizeFull();

  VerticalLayout infoLayout = createInfoLayout();
  VerticalLayout passwordChangeLayout = createPasswordChangeLayout();
  VerticalLayout deleteLayout = createDeleteLayout();

  passwordChangeLayout.setVisible(false);
  deleteLayout.setVisible(false);

  Button accountInformationButton = new Button("Account Information");
  accountInformationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
  accountInformationButton.addClickListener(e -> {
    infoLayout.setVisible(true);
    passwordChangeLayout.setVisible(false);
    deleteLayout.setVisible(false);
  });
  Tab accountInformation = new Tab(accountInformationButton);

  Button changePasswordButton = new Button("Change Password");
  changePasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
  changePasswordButton.addClickListener(e -> {
    infoLayout.setVisible(false);
    passwordChangeLayout.setVisible(true);
    deleteLayout.setVisible(false);
  });
  Tab changePassword = new Tab(changePasswordButton);

  Button deleteAccountButton = new Button("Delete Account");
  deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
  deleteAccountButton.addClickListener(e -> {
    infoLayout.setVisible(false);
    passwordChangeLayout.setVisible(false);
    deleteLayout.setVisible(true);
  });
  Tab deleteAccount = new Tab(deleteAccountButton);

  Tabs tabs = new Tabs(accountInformation, changePassword, deleteAccount);
  tabs.setOrientation(Tabs.Orientation.VERTICAL);

  menuLayout.add(tabs, infoLayout, passwordChangeLayout, deleteLayout);

  return menuLayout;
}

// @Tag("paper-card")
// @NpmPackage(value = "@polymer/paper-card", version = "3.0.1")
// @NpmPackage(value = "@polymer/paper-button", version = "3.0.1")
// @JsModule("@polymer/paper-card/paper-card.js")
// @JsModule("@polymer/paper-button/paper-button.js")

private VerticalLayout createInfoLayout() {
    VerticalLayout infoLayout = new VerticalLayout();
    infoLayout.setAlignItems(Alignment.STRETCH);
    infoLayout.setPadding(true);
    infoLayout.setSpacing(true);
    infoLayout.getStyle().set("border-radius", "10px");

    Span username = new Span("Username: @" + user.getUsername());
    
    TextField displayName = new TextField("Display Name");
    displayName.setClearButtonVisible(true);
    displayName.setValue(user.getDisplayName());
    displayName.setReadOnly(true);
    
    // TextField username = new TextField("Username");
    // username.setClearButtonVisible(true);
    // username.setValue(user.getUsername());
    // username.setReadOnly(true);
    
    TextField email = new TextField("Email");
    email.setClearButtonVisible(true);
    email.setValue(user.getEmail());
    email.setReadOnly(true);
    
    Button editButton = new Button("Edit");
    editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    editButton.getStyle().set("margin", "0 25%");
    editButton.addClickListener(event -> {
    boolean isReadOnly = !email.isReadOnly();
      // Change the color of the edit button depending on the mode
      displayName.setReadOnly(isReadOnly);
      //username.setReadOnly(isReadOnly);
      email.setReadOnly(isReadOnly);
      //password.setReadOnly(isReadOnly);
      editButton.setText(!isReadOnly ? "Save" : "Edit");
      editButton.getStyle().set("border", "none");
      editButton.getStyle().set("box-shadow", "0 0 5px black");
      // Remove the previous variant and add the new one
      editButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
      editButton.addThemeVariants(!isReadOnly ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_PRIMARY);

      //Change info
      Binder<User> binder = new Binder<>(User.class);
      binder.readBean(user);

      
      doBindFormToValidationRules(binder, displayName, email);

      try {
        // perform validation
        // update the user object with data from the fields
        binder.writeBean(user);
      } catch (ValidationException e) {
        System.out.println("Validation ERROR: Check that all validation rules are passed!");
        return;
      }
      if (binder.isValid()) {
        System.out.println("Validations all passed!");
        // retrieve the user record with this token
        Long uid = user.getUid();
        System.out.println("user "+ uid + " info changed");
        // reset info
        customUserDetailsService.updateUser(user);
      }
    });
    
    infoLayout.add(username, displayName, email, editButton);
    return infoLayout;
  }

  private VerticalLayout createPasswordChangeLayout() {
    //Password Layout
    VerticalLayout passwordChangeLayout = new VerticalLayout();
    passwordChangeLayout.setAlignItems(Alignment.STRETCH);
    passwordChangeLayout.setPadding(true);
    passwordChangeLayout.setSpacing(true);
    passwordChangeLayout.getStyle().set("border-radius", "10px");

    PasswordField currentPassword = new PasswordField("Current Password");

    Span currentPasswordError = new Span("");
    currentPasswordError.getStyle().set("color", "red");

    PasswordField newPassword = new PasswordField("New Password");
    PasswordField newPasswordConfirm = new PasswordField("Re-enter New Password");

    Button resetPasswordButton = new Button("Reset Password");
    resetPasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    resetPasswordButton.getStyle().set("box-shadow", "0 0 5px black");
    resetPasswordButton.getStyle().set("margin", "0 25%");

    Span successMessage = new Span("");

    //Password Change Feature
    Binder<User> binder = new Binder<>(User.class);
    binder.readBean(user);
    doBindFormToPasswordValidationRules(binder, newPassword, newPasswordConfirm);

    resetPasswordButton.addClickListener(event -> {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    if(passwordEncoder.matches(currentPassword.getValue(), user.getPassword())) {
      System.out.println("Password Matches");

      try {
        // perform validation
        // update the newUser object with data from the fields
        binder.writeBean(user);
      } catch (ValidationException e) {
        System.out.println("Validation ERROR: Check that all validation rules are passed!");
        return;
      }
      if (binder.isValid()) {
        System.out.println("Validations all passed!");
        // retrieve the user record with this token
        Optional<User> existingUser = customUserDetailsService.getByUid(user.getUid());
        if (existingUser.isPresent()){
          Long uid = user.getUid();
          System.out.println("user "+ uid + "pass changed");
          // // set new password
          customUserDetailsService.updatePassword(user);
          //Success
          successMessage.setText("Password changed!");
        }
      }
        
    } else {
      System.out.println("Incorrect password");
      currentPasswordError.setText("* Incorrect password");
    }
    });

    passwordChangeLayout.add(currentPassword, currentPasswordError, newPassword,
      newPasswordConfirm, resetPasswordButton, successMessage);

    return passwordChangeLayout;
  }

  private VerticalLayout createDeleteLayout() {
    VerticalLayout deleteLayout = new VerticalLayout();
    deleteLayout.setAlignItems(Alignment.STRETCH);
    deleteLayout.setPadding(true);
    deleteLayout.setSpacing(true);
    deleteLayout.getStyle().set("border-radius", "10px");

    H3 title = new H3("Delete Your Account");

    Span info = new Span("When you delete your account, you will be removed from any shared projects and your personal projects will be deleted. This account will be permanently erased.");

    PasswordField currentPassword = new PasswordField("Enter Your Password");

    Span currentPasswordError = new Span("");
    currentPasswordError.getStyle().set("color", "red");

    Button deleteButton = new Button("Delete Your Account");
    deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
    deleteButton.getStyle().set("margin", "0 25%");
    deleteButton.getStyle().set("box-shadow", "0 0 10px black");

    deleteButton.addClickListener(event -> {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
     if(passwordEncoder.matches(currentPassword.getValue(), user.getPassword())) {
        deleteAccount();
      } else {
       System.out.println("Incorrect password");
       currentPasswordError.setText("* Incorrect password");
      }

    });

    //Functionality
    deleteLayout.add(title, info, currentPassword, currentPasswordError, deleteButton);
    
    return deleteLayout;
  }

  //Account info change
  private void doBindFormToValidationRules(Binder<User> binder, TextField displayName,
    TextField email) {
     // binding fields to user model object
     // Shorthand for cases without extra configuration
    binder.forField(displayName)
    .bind(User::getDisplayName,  User::setDisplayName);

    // Start by defining the Field instance to use
    // binder.forField(usernameField)
    // .asRequired("This field is required")
    // // e.g., check if the password meets the required complexity rules
    // .withValidator(value -> isValidUsername(value), "Username must be >=3 characters")
    // .withValidator(value -> isUserNameUnique(value), "That Username already exists. Try again with a different Username")
    // .bind(User::getUsername, User::setUsername);

    // Shorthand for cases without extra configuration
    binder.forField(email)
    .asRequired("This field is required")
    .withValidator(value -> isEmailAlreadyPresent(value), "That email address already exists.  Try again with a different Email address.")
    .withValidator(value -> isEmailValidByRegex(value),"Invalid email address")
    .withValidator(new EmailValidator("Invalid email address", false))
    .bind(User::getEmail,  User::setEmail);
  }

  private boolean isUserNameUnique(String username) {
    // is already present && then false
    System.out.println("isPresent(): "
     + username 
     + " : " 
     + customUserDetailsService.getByUsername(username).isPresent());
    return !(customUserDetailsService.getByUsername(username).isPresent() && user.getUsername() != username);
  }

  private boolean isValidUsername(String username) {
    if (username.isEmpty()) return false;
    // Perform validation logic and return true or false based on the result
    // e.g., check if the username meets the required criteria
    return !username.isEmpty() && username.length() >= 3;
  }

  public boolean isEmailAlreadyPresent(String emailValue) {
    return !(customUserDetailsService.getByEmail(emailValue).isPresent() && user.getEmail() != emailValue);
  }

 private boolean isEmailValidByRegex(String value) {
    Pattern pattern = Pattern.compile(this.emailValidationRegex);
    return pattern.matcher(value).matches();
  }

  //Password change
  private void doBindFormToPasswordValidationRules(Binder<User> binder,
  PasswordField passwordField, PasswordField passwordConfirmField) {

    // Shorthand for cases without extra configuration
    binder.forField(passwordField)
    .withValidator(value -> isValidPassword(value), "Password must be >=8 characters ")
    .bind(User::getPassword,  User::setPassword);

    // handle confirmPassword
    binder.forField(passwordConfirmField)
    .withValidator(value -> value.equals(passwordField.getValue()), "Passwords do not match.")
    .bind(User::getPassword,  User::setPassword);
  }

  private boolean isValidPassword(String password) {
    if (password.isEmpty()) return false;
    // Perform validation logic and return true or false based on the result
    // e.g., check if the password meets the required complexity rules
    return password.length() >= 8;
  }

  //Delete Account
  private void deleteAccount() {
    //remove from friends' friends lists
    List<Long> friends = user.getFriends();
    if(friends != null) {
      for(Long friendID : friends) {
        if (customUserDetailsService.getByUid(friendID).isPresent()){
          User friend = customUserDetailsService.getByUid(friendID).get();
          List<Long> friendFriends = friend.getFriends();
          friendFriends.remove(user.getUid());
          friend.setFriends(friendFriends);
          customUserDetailsService.update(friend);
        }
      }
    }

    //remove user from projects, delete any non-shared projects
    Set<String> projects = user.getProjects();
    if(projects != null) {
      for(String projectID : projects) {
        if(projectsService.findProjectById(projectID).isPresent()){
          Project project = projectsService.findProjectById(projectID).get();
          List<Long> projectParticipants = project.getParticipants();

          //check if owner
          if(project.getOwner() == user.getUid()) {
            if(projectParticipants == null || projectParticipants.isEmpty()) {
              projectsService.deleteTask(project);
            } else {
              project.setOwner(projectParticipants.get(0));
              projectParticipants.remove(0);
              project.setParticipants(projectParticipants);

              projectsService.updateProject(project);
            }
          } else {
            //remove from participants list
            projectParticipants.remove(user.getUid());
            project.setParticipants(projectParticipants);
          }
        }
      }
    }

    //delete account
    customUserDetailsService.delete(user.getUid());
    securityViewService.logOut();
    this.getUI().get().getPage().setLocation("");
  }
}