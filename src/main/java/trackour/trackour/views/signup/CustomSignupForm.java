package trackour.trackour.views.signup;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.RouterLink;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
import trackour.trackour.views.login.LoginPage;

public class CustomSignupForm extends FormLayout {
        
        private final String emailValidationRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";;

        private H3 title;

        private TextField displayName;
        private TextField usernameField;

        private EmailField email;

        private PasswordField passwordField;
        private PasswordField passwordConfirmField;

        private Span errorMessageField;

        private Button submitButtonElement;

        CustomUserDetailsService userService;

   public CustomSignupForm(CustomUserDetailsService userService) {
        this.userService = userService;

        this.title = new H3("Sign up");
        this.displayName = new TextField("Display name");
        this.usernameField = new TextField("Username");
        this.email = new EmailField("Email");

        passwordField = new PasswordField("Password");
        passwordConfirmField = new PasswordField("Confirm password");

        setRequiredIndicatorIsVisible(usernameField, email, passwordField,
                passwordConfirmField);

        errorMessageField = new Span();

        submitButtonElement = new Button("Sign up");
        submitButtonElement.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // link to signup page
        RouterLink loginLink = new RouterLink("or Login", LoginPage.class);

        add(title, usernameField, displayName, email, passwordField,
                passwordConfirmField, errorMessageField,
                submitButtonElement, loginLink);

        Binder<User> binder = new Binder<>(User.class);

        // create a temp User object to act as a dto to record registration credentials
        User newUser = new User();
        // Updates the value in each bound field component
        binder.readBean(newUser);
        
        doDesignForm();

        doBindFormToValidationRules(binder);

        submitButtonElement.addClickListener(ev -> {
                System.out.println("Submit pressed!!");

                try {
                        // perform validation
                        // update the newUser object with data from the fields
                        binder.writeBean(newUser);
                } catch (ValidationException e) {
                        System.out.println("Validation ERROR: Check that all validation rules are passed!");
                        UI.getCurrent().navigate("signup?error");
                        return;
                }
                if (binder.isValid()) {
                        System.out.println("Signup validations all passed!");
                        // if no errors occured during validation, register/call userservice registration method
                        System.out.println("Registering user is:");
                        userService.printUserObj(newUser);

                        userService.registerUser(newUser);
                        UI.getCurrent().navigate("redirect:/login");
                }
        });
   }

   public boolean isEmailAlreadyPresent(String emailValue) {
        return userService.getByEmail(emailValue).isPresent();
   }

   private boolean isEmailValidByRegex(String value) {
        Pattern pattern = Pattern.compile(this.emailValidationRegex);
        return pattern.matcher(value).matches();
}

private void doBindFormToValidationRules(Binder<User> binder) {
        // binding fields to user model object
        // Shorthand for cases without extra configuration
        binder.forField(displayName)
        .bind(User::getDisplayName,  User::setDisplayName);

        // Start by defining the Field instance to use
        binder.forField(usernameField)
        .asRequired("This field is required")
        // e.g., check if the password meets the required complexity rules
        .withValidator(value -> isValidUsername(value), "Username must be >=3 characters")
        .withValidator(value -> isUserNameUnique(value), "That Username already exists. Try again with a different Username")
        .bind(User::getUsername, User::setUsername);

        // Shorthand for cases without extra configuration
        binder.forField(email)
        .asRequired("This field is required")
        .withValidator(value -> !isEmailAlreadyPresent(value), "That email address already exists.  Try again with a different Email address.")
        .withValidator(value -> isEmailValidByRegex(value),"Invalid email address")
        .withValidator(new EmailValidator("Invalid email address", false))
        .bind(User::getEmail,  User::setEmail);

        // Shorthand for cases without extra configuration
        binder.forField(passwordField)
        .asRequired("This field is required")
        .withValidator(value -> isValidPassword(value), "Password must be >=8 characters ")
        .bind(User::getPassword,  User::setPassword);

        // handle confirmPassword
        binder.forField(passwordConfirmField)
        .asRequired("This field is required")
        .withValidator(value -> value.equals(passwordField.getValue()), "Passwords do not match.")
        .bind(User::getPassword,  User::setPassword);
}

private void doDesignForm() {
        // Max width of the Form
        setMaxWidth("500px");

        // Allow the form layout to be responsive.
        // On device widths 0-490px we have one column.
        // Otherwise, we have two columns.
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        // These components always take full width
        setColspan(title, 2);
        setColspan(email, 2);
        setColspan(errorMessageField, 2);
        setColspan(submitButtonElement, 2);
}

private boolean isUserNameUnique(String username) {
        // is already present && then false
        System.out.println("isPresent(): "
         + username 
         + " : " 
         + userService.getByUsername(username).isPresent());
        return !userService.getByUsername(username).isPresent();
   }

   private boolean isValidUsername(String username) {
        if (username.isEmpty()) return false;
        // Perform validation logic and return true or false based on the result
        // e.g., check if the username meets the required criteria
        return !username.isEmpty() && username.length() >= 3;
    }

    private boolean isValidPassword(String password) {
        if (password.isEmpty()) return false;
        // Perform validation logic and return true or false based on the result
        // e.g., check if the password meets the required complexity rules
        return password.length() >= 8;
    }

   public PasswordField getPasswordField() { return passwordField; }

   public PasswordField getPasswordConfirmField() { return passwordConfirmField; }

   public Span getErrorMessageField() { return errorMessageField; }

   public Button getSubmitButtonElement() { return submitButtonElement; }

   private void setRequiredIndicatorIsVisible(HasValueAndElement<?, ?>... components) {
       Stream
        .of(components)
                .forEach(component -> {
                        component.setRequiredIndicatorVisible(true);
                });
   }

}