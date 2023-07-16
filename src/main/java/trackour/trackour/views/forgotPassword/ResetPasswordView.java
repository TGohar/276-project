package trackour.trackour.views.forgotPassword;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.html.H3;
//import com.vaadin.flow.component.html.Span;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.models.CustomUserDetailsService;
import trackour.trackour.models.User;
import trackour.trackour.security.SecurityViewService;
//import trackour.trackour.views.signup.CustomSignupForm;

@Route("resetPassword")
@PageTitle("Reset Password")
@AnonymousAllowed

public class ResetPasswordView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver, HasUrlParameter<String> {

    @Autowired
    SecurityViewService securityViewService;

    @Autowired
    CustomUserDetailsService userService;

    ResetPasswordForm resetPasswordForm;

    User user;

    String token;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is already authenticated
        this.securityViewService.handleAnonymousOnly(beforeEnterEvent, true);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {}

    @Override
    public void setParameter(BeforeEvent event, String parameter){
        this.token = parameter;

        System.out.println("this.token: " + this.token);

        Optional<User> existingUser = userService.getByPasswordResetToken(parameter);

        if (existingUser.isPresent()) {
            this.user = existingUser.get();
            if (isResetLinkExpired(user)) {
                System.out.println("showing error page since token is expired");
                // else display an error page
                event.rerouteTo("error");
            };
            // delete token
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenCreatedAt(null);
            userService.update(user);
            this.resetPasswordForm = new ResetPasswordForm(userService, user);
            // Center the form
            setAlignItems(FlexComponent.Alignment.CENTER);

            add(resetPasswordForm);
        }
        else{
            System.out.println("showing error page since token is invalid");
            // else display an error page
            event.rerouteTo("error");
        }
    }

    private boolean isResetLinkExpired(User existingUser) {
        final Integer HRS24_IN_SECONDS = 86400;
        if (existingUser == null) return false;
        LocalDateTime currDateTime = LocalDateTime.now();
        var dateTimeDiff = existingUser.getPasswordResetTokenCreatedAt().until(currDateTime, ChronoUnit.SECONDS);
        System.out.println("getPasswordResetTokenCreatedAt(): " + existingUser.getPasswordResetTokenCreatedAt());
        return dateTimeDiff >= HRS24_IN_SECONDS;
    }

    public ResetPasswordView(SecurityViewService securityViewService, CustomUserDetailsService userService) {
        this.securityViewService = securityViewService;
        this.userService = userService;
    }
    
}
