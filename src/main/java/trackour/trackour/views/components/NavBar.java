package trackour.trackour.views.components;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.applayout.AppLayout.Section;
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
    private SecurityViewService securityViewHandler;
    private CustomUserDetailsService customUserDetailsService;
    private static String searchValue;
    private UserDetails sessionObject;
    private Tabs mobileViewTabs;
    private AppLayout nav;
    // private Boolean div300pxOrLess = false;
    // private Boolean window1024OrLess = false;

    public NavBar(CustomUserDetailsService customUserDetailsService, SecurityViewService securityViewHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityViewHandler = securityViewHandler;
        this.sessionObject = securityViewHandler.getAuthenticatedRequestSession();
        this.mobileViewTabs = new Tabs();
        this.nav = new AppLayout();
    }

    private void onClickTabRouteTo(Tab clickedElement, Class<? extends Component> navigationTarget) {
        clickedElement.getElement().addEventListener("click", event -> {
            routeTo(navigationTarget);
        });
    }

    private Tabs generateRouteTabs() {
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
        
        // set the mobile view drawer tabs
        this.mobileViewTabs.add(
            home,
            dashboard,
            friends,
            explore,
            advancedSearch
        );

        // if the session is an admin, reveal the link/tab to the secret page
        SimpleGrantedAuthority sessionAdminRoleObj = new SimpleGrantedAuthority(Role.ADMIN.roleToRoleString());
        if (sessionObject.getAuthorities().contains(sessionAdminRoleObj)) {
            this.mobileViewTabs.add(adminViewUsers);
        }
        return this.mobileViewTabs;
    }
        
    private HorizontalLayout generateRouteTabsLayout() {
        HorizontalLayout routeTabsArea = new HorizontalLayout();
        routeTabsArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        routeTabsArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        routeTabsArea.setWidthFull();
        // routeTabsArea.getStyle().set("background-color", "blue");

        Tabs navTabs = generateRouteTabs();
        // style navtabs
        navTabs.setWidthFull();
        navTabs.addThemeVariants(TabsVariant.MATERIAL_FIXED);
        routeTabsArea.add(navTabs);
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

    private HorizontalLayout generateMenuBar() {
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
    
    public Component getContent() {
        return nav.getContent();
    }

    /**
     * This is where the contents for the page using this navbar object will be placed.
     * You need to provide only one parent {@link Component} object to act as a container for every other content on the page
     * @param content
     */
    public void setContent(Component content) {
        nav.setContent(content);
        nav.getStyle().setWidth("100%");
        nav.getStyle().setHeight("100%");

        nav.getStyle().setBackground("red");
        
        nav.getElement().getStyle().set("name", "nav-content");
    }

    /**
     * this returns the actaul whole nav component
     * The isMobileView boolean is set to true if your view is in a mobile/smaller screen mode
     * @param isMobileView
     * @return
     */
    public AppLayout generateNavComponent(Boolean isMobileView) {

        DrawerToggle toggle = new DrawerToggle();

        nav.setPrimarySection(Section.NAVBAR);

        Tabs tabs = this.getMobileViewTabs();

        if (isMobileView) {
        nav.addToNavbar(this.generateNavBarComponent(), toggle);
            tabs.setOrientation(Tabs.Orientation.VERTICAL);
            nav.addToDrawer(tabs);
        }
        else {
            nav.addToNavbar(this.generateNavBarComponent());
        }
        nav.setDrawerOpened(false);
        nav.getStyle().setWidth("100%");
        return nav;
    }
    private HorizontalLayout generateNavBarComponent() {

        HorizontalLayout navHorizontalLayout = new HorizontalLayout();
        navHorizontalLayout.setPadding(true);
        navHorizontalLayout.setWidthFull();
        navHorizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navHorizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        // String x = "(max-width: 600px)";
        // query.addAction(() -> {
        //     // Do something when the media query matches
        // });
        navHorizontalLayout.add(
            generateLogo(),
            generateRouteTabsLayout(),
            generateMenuBar()
        );

        return navHorizontalLayout;
    }

    private Tabs getMobileViewTabs() {
        return mobileViewTabs;
    }

    public static String getSearchValue() {
        return searchValue;
    }
}
