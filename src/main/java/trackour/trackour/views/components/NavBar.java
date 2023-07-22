package trackour.trackour.views.components;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
// import com.vaadin.flow.component.UI;
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
    private UserDetails sessionObject;
    private Tabs viewTabs;
    private AppLayout navM;
    private AppLayout navW;
    Component content;
    // private Boolean div300pxOrLess = false;
    // private Boolean window1024OrLess = false;

    public NavBar(CustomUserDetailsService customUserDetailsService, SecurityViewService securityViewHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityViewHandler = securityViewHandler;
        this.sessionObject = securityViewHandler.getAuthenticatedRequestSession();
        this.viewTabs = new Tabs();
        this.navM = new AppLayout();
        this.navW = new AppLayout();
        generateRouteTabs();
        initWindowNav();
        initMobileNav();
    }

    private void initMobileNav() {
        DrawerToggle toggle = new DrawerToggle();
        // generateMobileRouteTabs();
        navM.setPrimarySection(Section.DRAWER);
        navM.setDrawerOpened(false);
        navM.getStyle().setWidth("100%");
        navM.getStyle().setHeight("100%");
        viewTabs.setOrientation(Tabs.Orientation.VERTICAL);
        navM.addToDrawer(
            viewTabs
            );
        navM.addToNavbar(toggle, generateLogo(), generateMenuBar());
    }
    
    private void initWindowNav() {
        navW.setPrimarySection(Section.NAVBAR);
        navW.getStyle().setWidth("100%");
        navW.getStyle().setHeight("100%");
        navW.addToNavbar(this.generateWindowNavBarComponent());
    }

    private void onClickTabRouteTo(Tab clickedElement, Class<? extends Component> navigationTarget) {
        clickedElement.getElement().addEventListener("click", event -> {
            SecurityViewService.routeTo(navigationTarget);
        });
    }

    private void generateRouteTabs() {
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
        this.viewTabs.add(
            home,
            dashboard,
            friends,
            explore,
            advancedSearch
        );
        // if the session is an admin, reveal the link/tab to the secret page
        SimpleGrantedAuthority sessionAdminRoleObj = new SimpleGrantedAuthority(Role.ADMIN.roleToRoleString());
        if (sessionObject.getAuthorities().contains(sessionAdminRoleObj)) {
            this.viewTabs.add(adminViewUsers);
        }
    }
        
    private HorizontalLayout generateWindowRouteTabsLayout() {
        HorizontalLayout routeTabsArea = new HorizontalLayout();
        routeTabsArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        routeTabsArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        routeTabsArea.setWidthFull();
        
        // style navtabs
        viewTabs.setWidthFull();
        viewTabs.addThemeVariants(TabsVariant.MATERIAL_FIXED);
        routeTabsArea.add(viewTabs);
        return routeTabsArea;
    }

    private HorizontalLayout generateLogo() {
        HorizontalLayout logoArea = new HorizontalLayout();
        logoArea.setWidthFull();
        logoArea.setAlignSelf(FlexComponent.Alignment.CENTER);
        logoArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 logo = new H1("Trackour");
        // logo.getStyle().set("background-color", "blue");
        logo.addClickListener(ev -> SecurityViewService.routeTo(HomeView.class));
        logo.getStyle().setCursor("pointer");
        logoArea.add(logo);
        return logoArea;
    }

    private HorizontalLayout generateMenuBar() {
        String sessionUsername = sessionObject.getUsername();
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();

        HorizontalLayout horizontalMenuArea = new HorizontalLayout();
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
    
    public Component getContent() {
        return navW.getContent();
    }

    /**
     * This is where the contents for the page using this navbar object will be placed.
     * You need to provide only one parent {@link Component} object to act as a container for every other content on the page
     * @param content
     */
    public void setContent(Component content) {
        this.content = content;
        navW.setContent(content);
        navM.setContent(content);
    }

    public void clearContent() {
        this.content = null;
        navM.setContent(this.content);
        navM.setContent(this.content);
    }

    /**
     * this returns the actaul whole nav component
     * The isMobileView boolean is set to true if your view is in a mobile/smaller screen mode
     * @param isMobileView
     * @return
     */
    public AppLayout generateNavComponent() {
        
        // if (!isMobileView) {
        //     return navW;
        // }
        return navM;
    }
    private HorizontalLayout generateWindowNavBarComponent() {

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
            generateWindowRouteTabsLayout(),
            generateMenuBar()
        );

        return navHorizontalLayout;
    }
}
