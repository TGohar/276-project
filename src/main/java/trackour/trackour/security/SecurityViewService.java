package trackour.trackour.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;

import trackour.trackour.model.Role;

/**
 * This class includes "View-manipulation" methods
 * such as one to logout user, and methods for rerouting
 * to logout current user with {@code logOut()}.
 */
@Service
public class SecurityViewService {

    @Autowired
    private final AuthenticationContext authContext;

    SecurityViewService(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    /**
     * Call this method to set any view as only accessible by
     * anonymous or unauthenticated users and admins only
     * 
     */
    public void handleAnonymousOnly(BeforeEnterEvent beforEnterEvent, Boolean excludeFromPage) {
        // only anonymous user sessions and admins are allowed
        boolean isAuthenticatedUser = getSessionOptional().isPresent();
        Optional<UserDetails> userSession = getSessionOptional();
        boolean isUserAdmin = false;

        if (userSession.isPresent()) {
            isUserAdmin = userSession.get().getAuthorities()
                    .contains(new SimpleGrantedAuthority(Role.ADMIN.roleToRoleString()));
        }

        if (isAuthenticatedUser) {
            // allow this page to bypass the redirection protocol
            if (excludeFromPage && isUserAdmin)
                return;
            beforEnterEvent.rerouteTo("error");
            return;
        }
    }

    /**
     * Call to route to a specific view/navigation target
     * @param navigationTarget
     */
    public static void routeTo(Class<? extends Component> navigationTarget) {
        UI.getCurrent().navigate(navigationTarget);
    }
    public static void routeTo(String navigationTarget) {
        UI.getCurrent().navigate(navigationTarget);
    }

    public void logOut() {
        UI.getCurrent().getPage().setLocation("/login");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

    /**
     * Call to get current authenticated user info
     * 
     * @return Optional with current authenticated user, else empty
     */
    public UserDetails getAuthenticatedRequestSession() {
        return this.getSessionOptional().get();
    }

    public Optional<UserDetails> getSessionOptional() {
        return authContext.getAuthenticatedUser(UserDetails.class);
    }
}
