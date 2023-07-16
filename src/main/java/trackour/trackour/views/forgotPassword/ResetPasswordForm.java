package trackour.trackour.views.forgotPassword;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.RouterLink;

import trackour.trackour.models.CustomUserDetailsService;
import trackour.trackour.models.User;
import trackour.trackour.views.login.LoginPage;

public class ResetPasswordForm extends VerticalLayout {

    @Autowired
    CustomUserDetailsService userService;

    User user;

    private H3 title;
    
    private PasswordField passwordField;
    private PasswordField passwordConfirmField;

    private Span errorMessageField;

    private Button submit;

    RouterLink loginLink;

    public ResetPasswordForm(
        CustomUserDetailsService userService, 
        User user){
        this.userService = userService;
        this.user = user;

        this.title = new H3("Reset your password");

        this.passwordField = new PasswordField("New Password");
        this.passwordConfirmField = new PasswordField("Confirm Password");

        this.passwordField.setRequiredIndicatorVisible(true);
        this.passwordConfirmField.setRequiredIndicatorVisible(true);

        this.errorMessageField = new Span();

        this.submit = new Button("Reset Password");
        this.submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //link to login page
        this.loginLink = new RouterLink("return to login page", LoginPage.class);

        add(title, doFormLayout(), errorMessageField, loginLink);

        Binder<User> binder = new Binder<>(User.class);

        binder.readBean(user);

        doBindFormToValidationRules(binder);

        submit.addClickListener(ev -> {
                System.out.println("Submit pressed!!");

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
                        // if no errors occured during validation, register/call userservice registration method
                        System.out.println("Updating user is:");
                        user.setPasswordResetToken(null);
                        userService.updatePassword(user);
                        UI.getCurrent().navigate("redirect:/login");
                }
        });
    }
    
    private FormLayout doFormLayout() {
        FormLayout layout = new FormLayout();

        setMaxWidth("500px");

        setMargin(true);

        setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(passwordField, passwordConfirmField, submit);

        return layout;
    }

    private void doBindFormToValidationRules(Binder<User> binder) {
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

    private boolean isValidPassword(String password) {
        if (password.isEmpty()) return false;
        // Perform validation logic and return true or false based on the result
        // e.g., check if the password meets the required complexity rules
        return password.length() >= 8;
    }

    public PasswordField getPasswordField() { return passwordField; }

    public PasswordField getPasswordConfirmField() { return passwordConfirmField; }

    public Span getErrorMessageField() { return errorMessageField; }

    public Button getSubmitButton() { return submit; }
}
