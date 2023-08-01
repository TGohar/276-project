package trackour.trackour.views.forgotPassword;

import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.login.LoginPage;

@Route("resetPassword")
@RouteAlias("reset")
@PageTitle("Forgot Password - Enter Email | Trackour")
@AnonymousAllowed
public class enterEmailView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver {

    @Autowired
    SecurityViewService securityService;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    // PasswordTokenService passwordTokenService;

    private String mailHost;
    private Integer mailPort;
    private String mailUsername;
    private String mailPassword;

    public enterEmailView(
            SecurityViewService securityService,
            CustomUserDetailsService customUserDetailsService,
            @Value("${mail.smtp.host}") String mailHost,
            @Value("${mail.smtp.port}") Integer mailPort,
            @Value("${mail.smtp.username}") String mailUsername,
            @Value("${mail.smtp.password}") String mailPassword) {

        this.securityService = securityService;
        this.customUserDetailsService = customUserDetailsService;

        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        // this.passwordTokenService = passwordTokenService;

        H3 title = new H3("Enter your email");

        Span text = new Span(
                "An email with a link to reset your password will be sent shortly. Remember to check your spam folder!");

        EmailField emailField = new EmailField("Email");

        Span error = new Span("");

        Button submit = new Button("Submit", e -> getEmail(emailField.getValue(), error));
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink loginLink = new RouterLink("return to login page", LoginPage.class);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setMargin(true);
        layout.setMaxWidth("500px");

        setAlignItems(FlexComponent.Alignment.CENTER);

        FormLayout form = new FormLayout();
        form.add(emailField, submit);
        form.setColspan(emailField, 1);
        form.setColspan(submit, 1);

        layout.add(title, text, form, error, loginLink);

        add(layout);
    }

    public void getEmail(String email, Span error) {

        User user;

        System.out.println("Searching for user...");

        if (customUserDetailsService.getByEmail(email).isPresent()) {
            user = customUserDetailsService.getByEmail(email).get();
            System.out.println("User found: " + user.getUsername());
            error.setText("The password reset email has been sent!");
            // update user record with new password token

            // send the email

            // async method to send email w link
            getUI().get().getPage().fetchCurrentURL(currentUrl -> {
                // create a new token whenever a reset request is sent to invalidate any old
                // token that may have not been used
                // this is to avoid stolen tokens from remaining valid
                user.generatePasswordResetToken();
                String token = user.getPasswordResetToken();
                System.out.println("Password token: " + token);
                System.out.println("currentUrl: " + currentUrl);
                // mark token timestamp
                user.setPasswordResetTokenCreatedAt(LocalDateTime.now());
                customUserDetailsService.update(user);
                sendResetLink(user.getUsername(), currentUrl, token, email);
            });

            // PasswordToken token = new PasswordToken();
            // passwordTokenService.newToken(token);

        } else {
            System.out.println("Could not find user");
            error.setText("Email not found! Please try a different email.");
        }
    }

    void sendResetLink(String username, URL currentUrl, String token, String recipientEmail) {
        String resetLink = currentUrl.toString() + "/" + token;
        System.out.println("link: " + resetLink);
        ResetLinkService resetLinkHandler = new ResetLinkService(
                this.mailHost,
                this.mailPort,
                this.mailUsername,
                this.mailPassword,
                recipientEmail,
                resetLink);

        resetLinkHandler.sendEmail(username);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is
        // already authenticated
        this.securityService.handleAnonymousOnly(beforeEnterEvent, true);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
    }

}
