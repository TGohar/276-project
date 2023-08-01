package trackour.trackour.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.signup.SignupPageView;

@Route("login")
@RouteAlias("login")
@PageTitle("Login | Trackour")
@AnonymousAllowed

public class LoginPage extends VerticalLayout implements BeforeLeaveObserver, BeforeEnterObserver {
    
    SecurityViewService securityViewHandler;

    private LoginForm login = new LoginForm();

    // inject view auth handler
    public LoginPage(SecurityViewService securityViewHandler) {
        this.securityViewHandler = securityViewHandler;

        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // submit action
        login.setAction("login");

        login.addForgotPasswordListener(ev -> {
            UI.getCurrent().navigate("resetPassword");
        });

        // link to signup page
        RouterLink signUpLink = new RouterLink("or Signup", SignupPageView.class);

        add(new H1("Trackour"), login, signUpLink);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // this method call reroutes get requests to this view if the current session is already authenticated
        // getUI().get().getPage().addJavaScript("window.location.href = 'myurl'");
        this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, false);
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
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
