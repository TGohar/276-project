package trackour.trackour.views.signup;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.models.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

@Route("signup")
@PageTitle("SignUp")
@AnonymousAllowed
public class SignupPageView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver  {

    @Autowired
    SecurityViewService securityViewHandler;

    @Autowired
    CustomUserDetailsService userService;
    
    CustomSignupForm signupForm;
   
    public SignupPageView(SecurityViewService securityViewHandler, CustomUserDetailsService userService) {
        this.userService = userService;
        this.securityViewHandler = securityViewHandler;

        this.signupForm = new CustomSignupForm(this.userService);
        
        // Center the signupForm
        setHorizontalComponentAlignment(Alignment.CENTER, signupForm);
        add(signupForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is already authenticated or "excludeFromPage" is true
        this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, true);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) { }
}