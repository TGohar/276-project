package trackour.trackour.views.components;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
// import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.Role;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.admin.AdminUsersView;
import trackour.trackour.views.explore.ExploreView;
import trackour.trackour.views.friends.FriendsView;
import trackour.trackour.views.home.HomeView;

public class NavBar {
    SecurityViewService securityViewHandler;
    CustomUserDetailsService customUserDetailsService;
    static String searchValue;
    UserDetails sessionObject;

    public NavBar(CustomUserDetailsService customUserDetailsService, SecurityViewService securityViewHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityViewHandler = securityViewHandler;
        this.sessionObject = securityViewHandler.getAuthenticatedRequestSession();
    }

    private void onClickTabRouteTo(Tab clickedElement, Class<? extends Component> navigationTarget) {
        clickedElement.getElement().addEventListener("click", event -> {
            routeTo(navigationTarget);
        });
    }

    private HorizontalLayout generateRouteTabs() {
        HorizontalLayout routeTabsArea = new HorizontalLayout();
        routeTabsArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        routeTabsArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        routeTabsArea.setWidthFull();
        // routeTabsArea.getStyle().set("background-color", "blue");

        Tabs routeTabs = new Tabs();
        routeTabs.setWidthFull();
        // routeTabs.getStyle().set("background-color", "red");

        routeTabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        // friendsTab.setEnabled(false);
        Tab home = new Tab("Home");
        home.addAttachListener(ev -> onClickTabRouteTo(home, HomeView.class));

        Tab dashboard = new Tab("Dashboard");
        dashboard.addAttachListener(ev -> onClickTabRouteTo(dashboard, HomeView.class));
        dashboard.setEnabled(false);
        
        Tab friends = new Tab("Friends");
        friends.addAttachListener(ev -> onClickTabRouteTo(friends, FriendsView.class));
        
        Tab explore = new Tab("Explore");
        explore.addAttachListener(ev -> onClickTabRouteTo(explore, ExploreView.class));
        
        Tab advancedSearch = new Tab("Advanced Search");
        advancedSearch.addAttachListener(ev -> onClickTabRouteTo(advancedSearch, HomeView.class));
        advancedSearch.setEnabled(false);
        
        Tab adminViewUsers = new Tab("Admin View Users");
        adminViewUsers.addAttachListener(ev -> onClickTabRouteTo(adminViewUsers, AdminUsersView.class));
        
        routeTabs.add(
            home,
            dashboard,
            friends,
            explore,
            advancedSearch
        );
        // if the session is an admin, reveal the link/tab to the secret page
        SimpleGrantedAuthority sessionAdminRoleObj = new SimpleGrantedAuthority(Role.ADMIN.roleToRoleString());
        if (sessionObject.getAuthorities().contains(sessionAdminRoleObj)) {
            routeTabs.add(adminViewUsers);
        }

        routeTabsArea.add(routeTabs);
        return routeTabsArea;
    }

    private HorizontalLayout generateLogo() {
        HorizontalLayout logoArea = new HorizontalLayout();
        logoArea.setWidthFull();
        logoArea.setAlignSelf(FlexComponent.Alignment.CENTER);
        logoArea.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        // logoArea.getStyle().set("background-color", "red");

        H1 logo = new H1("Trackour");
        // logo.getStyle().set("background-color", "blue");
        logo.addClickListener(ev -> routeTo(HomeView.class));
        logo.getStyle().setCursor("pointer");
        logoArea.add(logo);
        return logoArea;
    }

    public HorizontalLayout generateMenuBar() {
        String sessionUsername = sessionObject.getUsername();
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();

        HorizontalLayout horizontalMenuArea = new HorizontalLayout();
        horizontalMenuArea.setWidthFull();
        horizontalMenuArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        horizontalMenuArea.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        H5 h5 = new H5(displayNameString);
        MenuBar menuBar = new MenuBar();
        Avatar avatarName = new Avatar(displayNameString);
        MenuItem item = menuBar.addItem(avatarName);
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        SubMenu subMenu = item.getSubMenu();
        subMenu.addItem("Profile").setEnabled(false);
        subMenu.add(new Hr());
        subMenu.addItem("Sign out")
        .addClickListener(event -> {
            securityViewHandler.logOut();
        });
        horizontalMenuArea.add(h5, menuBar);
        return horizontalMenuArea;
    }

    private void routeTo(Class<? extends Component> navigationTarget) {
        UI.getCurrent().navigate(navigationTarget);
    }

    public HorizontalLayout generateComponent() {

        HorizontalLayout navHorizontalLayout = new HorizontalLayout();
        navHorizontalLayout.setPadding(true);
        navHorizontalLayout.setWidthFull();
        navHorizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navHorizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        navHorizontalLayout.add(
            generateLogo(),
            generateRouteTabs(),
            generateMenuBar()
        );

        return navHorizontalLayout;
    }

    // private static class RouteTabs extends Tabs implements BeforeEnterObserver {
    //     private final Map<RouterLink, Tab> routerLinkTabMap = new HashMap<>();

    //     public void add(RouterLink routerLink) {
    //         routerLink.setHighlightCondition(HighlightConditions.sameLocation());
    //         routerLink.setHighlightAction(
    //                 (link, shouldHighlight) -> {
    //                     if (shouldHighlight)
    //                         setSelectedTab(routerLinkTabMap.get(routerLink));
    //                 });
    //         routerLinkTabMap.put(routerLink, new Tab(routerLink));
    //         add(routerLinkTabMap.get(routerLink));
    //     }

    //     @Override
    //     public void beforeEnter(BeforeEnterEvent event) {
    //         // In case no tabs will match
    //         setSelectedTab(null);
    //     }
    // }

    public static String getSearchValue(){
            return searchValue;
        } 
}
