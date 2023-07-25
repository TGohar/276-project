package trackour.trackour.views.forgotPassword;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
import trackour.trackour.security.SecurityViewService;

@Route(value = "resetPassword")
@RouteAlias("new-password")
@PageTitle("Set New Password | Trackour")
@AnonymousAllowed
public class ResetPasswordView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver, HasUrlParameter<String> {

    @Autowired
    SecurityViewService securityViewHandler;

    @Autowired
    CustomUserDetailsService userService;

    ResetPasswordForm resetPasswordForm;
    
    User user;

    String token;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is already authenticated
        this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, true);
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        // reroute to error page
        if (event.hasUnknownReroute()){
            System.out.println("Rerouting to Error Page!");
        }
    }

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

    public ResetPasswordView(
        SecurityViewService securityViewHandler,
        CustomUserDetailsService userService) {
        this.userService = userService;
        this.securityViewHandler = securityViewHandler;
    }
    
}
