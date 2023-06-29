package trackour.trackour.security;

// import javax.ws.rs.core.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Concerned with information used to check what roles the given user
 * has as well as store the principal which tells you what user is
 * currentlty authenticated/logged in
 * 
 */
public class CustomSecurityContext extends HttpServletRequestWrapper {

    public CustomSecurityContext(HttpServletRequest request) {
        super(request);
        this.isUserInRole(BASIC_AUTH);
        //TODO Auto-generated constructor stub
    }

    // private final String[] userRoles;
    // CustomSecurityContext(String [] userRoles, String user){
}
