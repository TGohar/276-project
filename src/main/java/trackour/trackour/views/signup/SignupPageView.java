package trackour.trackour.views.signup;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

@Route("signup")
@RouteAlias("register")
@PageTitle("SignUp | Trackour")
@AnonymousAllowed
public class SignupPageView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver  {

    @Autowired
    SecurityViewService securityViewHandler;

    @Autowired
    CustomUserDetailsService userService;
    
    CustomSignupForm signupForm;
   
    public SignupPageView(SecurityViewService securityViewHandler, CustomUserDetailsService userService) {

        this.signupForm = new CustomSignupForm(userService);
        
        // Center the signupForm
        setHorizontalComponentAlignment(Alignment.CENTER, signupForm);

        add(signupForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is already authenticated
        this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, true);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {}
}