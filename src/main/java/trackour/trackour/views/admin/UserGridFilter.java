package trackour.trackour.views.admin;
import java.util.Set;

import com.vaadin.flow.component.grid.dataview.GridListDataView;

import trackour.trackour.model.user.Role;
import trackour.trackour.model.user.User;

class UserGridFilter {
    private final GridListDataView<User> dataView;

    private String uid;
    private String displayName;
    private String username;
    private String email;
    private String role;

    public UserGridFilter(GridListDataView<User> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::doFilter);
    }

    public void setUid(String uid) {
        this.uid = uid;
        this.dataView.refreshAll();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.dataView.refreshAll();
    }

    public void setUsername(String username) {
        this.username = username;
        this.dataView.refreshAll();
    }

    public void setEmail(String email) {
        this.email = email;
        this.dataView.refreshAll();
    }

    public void setRole(String role) {
        this.role = role;
        this.dataView.refreshAll();
    }

    public boolean doFilter(User user) {
        boolean matchesUid = matches(user.getUid().toString(), uid);
        boolean matchesDisplayName = matches(user.getDisplayName(), displayName);
        boolean matchesUsername = matches(user.getUsername(), username);
        boolean matchesEmail = matches(user.getEmail(), email);
        boolean matchesRole = matchesRole(user.getRoles(), this.role);

        return (matchesUid && matchesUsername && matchesDisplayName && matchesEmail && matchesRole);
    }



    private boolean matchesRole(Set<Role> recordRoles, String queryRole) {
        boolean itContainsRole = false;
        // filter by queryRole String
        try {
            if (queryRole == null || queryRole.isEmpty() || recordRoles.contains(Role.valueOf(queryRole.toUpperCase()))){
                itContainsRole = true;
            }
        } catch (IllegalArgumentException e) {
            itContainsRole = true;
        }
        return itContainsRole;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
