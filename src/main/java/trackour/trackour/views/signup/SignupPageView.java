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

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewHandler;

@Route("signup")
@PageTitle("SignUp")
@AnonymousAllowed
public class SignupPageView extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver  {

    @Autowired
    SecurityViewHandler securityViewHandler;

    @Autowired
    CustomUserDetailsService userService;
    
    CustomSignupForm signupForm;
   
    public SignupPageView(SecurityViewHandler securityViewHandler, CustomUserDetailsService userService) {

        this.signupForm = new CustomSignupForm(userService);
        
        // Center the signupForm
        setHorizontalComponentAlignment(Alignment.CENTER, signupForm);

        add(signupForm);
    }

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
}