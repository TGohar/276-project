package trackour.trackour.views.components;
// import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.Role;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.admin.AdminUsersView;
import trackour.trackour.views.components.responsive.ResponsiveLayout;
import trackour.trackour.views.explore.ExploreView;
import trackour.trackour.views.friends.FriendsView;
import trackour.trackour.views.home.HomeView;

public class NavBar extends ResponsiveLayout {
    // constant for the browser width threshold. < 600 == smaller screens
    private static final int BROWSER_WIDTH_THRESHOLD = 800;

    @Autowired
    private SecurityViewService securityViewHandler;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // app layout is the base container for this responsive component
    private AppLayout appLayout; // it's the navbar itself and this has a content object where the page contents are stored

    // the layout that contains the navbar tabs and the toggle button respectively
    private HorizontalLayout navbarTabsLayout;
    private DrawerToggle drawerToggle;

    // the current session object
    private UserDetails sessionObject;

    // continer for the page's main content
    VerticalLayout contentContainer;
    Component content;

    public NavBar(CustomUserDetailsService customUserDetailsService, SecurityViewService securityViewHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityViewHandler = securityViewHandler;
        this.sessionObject = securityViewHandler.getAuthenticatedRequestSession();
        
        // Init the app layout element
        appLayout = new AppLayout();
        appLayout.getStyle().setWidth("100%");
        appLayout.getStyle().setHeight("100%");

        // init the drawer toggle element
        drawerToggle = new DrawerToggle();

        // init the logo element
        HorizontalLayout logoLayout = generateLogo();

        // Init the horizontal layout for the navbar tabs element
        navbarTabsLayout = generateNavbarLayout();

        Tabs drawerTabs = generateDrawerTabs();

        // menu buton with icon and lo==signout button
        HorizontalLayout menuBarArea = generateMenuBar();

        // Add the drawer toggle, logo layout and navbar tabs layout elements to the navbar section of the app layout
        appLayout.addToNavbar(drawerToggle, logoLayout, navbarTabsLayout, menuBarArea);

        // Add the drawer tabs element to the drawer section of the app layout
        appLayout.addToDrawer(drawerTabs);

        // Set the initial layout mode and drawer visibility of the app layout
        appLayout.setPrimarySection(AppLayout.Section.NAVBAR);
        appLayout.setDrawerOpened(false);

        // Add the app layout element to the container element of the ResponsiveLayout component
        add(appLayout);
    }

    private void onClickTabRouteTo(Tab clickedElement, Class<? extends Component> navigationTarget) {
        if (navigationTarget.getClass().equals(ExploreView.class)) {
            System.out.println("Click Explore!");
        }
        clickedElement.getElement().addEventListener("click", event -> {
            SecurityViewService.routeTo(navigationTarget);
        });
    }

    private Tab[] generateTabArray() {
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

        // if the session is an admin, reveal the link/tab to the secret page
        SimpleGrantedAuthority sessionAdminRoleObj = new SimpleGrantedAuthority(Role.ADMIN.roleToRoleString());
        if (sessionObject.getAuthorities().contains(sessionAdminRoleObj)) {
            return  new Tab[] {
                home,
                dashboard,
                friends,
                explore,
                advancedSearch,
                adminViewUsers
            };
        }
        
        return  new Tab[] {
            home,
            dashboard,
            friends,
            explore,
            advancedSearch
        };
    }

    private Tabs generateDrawerTabs() {
        // Use the same helper method to create another tabs component with vertical orientation and some theme variants
        Tabs drawerTabs = createTabs(
            Tabs.Orientation.VERTICAL,
            new TabsVariant[] {TabsVariant.LUMO_SMALL, TabsVariant.LUMO_MINIMAL},
            generateTabArray()
        );
        // Add custom theme variants to the tabs component
        // drawerTabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS, TabsVariant.LUMO_MINIMAL);
        return drawerTabs;
    }
        
    private HorizontalLayout generateNavbarLayout() {
        HorizontalLayout routeTabsArea = new HorizontalLayout();
        routeTabsArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        routeTabsArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // routeTabsArea.setWidthFull();
        // routeTabsArea.getStyle().setBackground("red");

        // generate tabs for navbar
        Tabs navbarTabs = createTabs(
            Tabs.Orientation.HORIZONTAL,
            new TabsVariant[] {TabsVariant.LUMO_SMALL, TabsVariant.LUMO_MINIMAL}, 
            generateTabArray()    
        );
        // Add custom theme variants to the tabs component
        // navbarTabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS, TabsVariant.LUMO_MINIMAL);
        
        // style navtabs
        navbarTabs.setWidthFull();

        navbarTabs.addThemeVariants(TabsVariant.MATERIAL_FIXED);
        routeTabsArea.add(navbarTabs);
        return routeTabsArea;
    }

    // helper method to return a tabs component with a given orientation, theme variants an array of tab objects
    private Tabs createTabs(
        Tabs.Orientation orientation, 
        TabsVariant[] variantsArray, 
        Tab... tabsArray) {
        // init tabs object
        Tabs tabsComponent = new Tabs();
        // set orientation
        tabsComponent.setOrientation(orientation);
        // add theme variants
        tabsComponent.addThemeVariants(variantsArray);
        // add all tab objects
        tabsComponent.add(tabsArray);
        // Return the tabs
        return tabsComponent;
    }

    private HorizontalLayout generateLogo() {
        // Create a horizontal layout for the logo element
        HorizontalLayout logoArea = new HorizontalLayout();
        logoArea.setAlignSelf(FlexComponent.Alignment.CENTER);
        logoArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 logo = new H1("Trackour");
        // logo.getStyle().set("background-color", "blue");
        logo.addClickListener(ev -> SecurityViewService.routeTo(HomeView.class));
        logo.getStyle().setCursor("pointer");
        logoArea.add(logo);
        // logoArea.getStyle().setBackground("cyan");
        logoArea.setWidthFull();
        return logoArea;
    }

    private HorizontalLayout generateMenuBar() {
        String sessionUsername = sessionObject.getUsername();
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();

        HorizontalLayout horizontalMenuArea = new HorizontalLayout();
        horizontalMenuArea.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        horizontalMenuArea.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        horizontalMenuArea.setSizeFull();
        

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
        horizontalMenuArea.setWidthFull();
        horizontalMenuArea.add(h5, menuBar);
        return horizontalMenuArea;
    }

    public AppLayout getNav() {
        return appLayout;
    }

    /**
     * This is where the contents for the page using this navbar object will be placed.
     * You need to provide only one parent {@link Component} object to act as a container for every other content on the page
     * @param content
     */
    public void setContent(Component content) {
        contentContainer = new VerticalLayout();
        contentContainer.setSizeFull();
        contentContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        contentContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.content = content;
        this.content.getStyle().setHeight("100%");
        this.content.getStyle().setWidth("100%");
        
        // this.content.getStyle().setBackground("red");
        contentContainer.add(this.content);
        appLayout.setContent(contentContainer);
    }

    public void clearContent() {
        this.contentContainer = null;
        this.content = null;
        appLayout.setContent(this.content);
    }

    @Override
    protected void update(int browserWidth) {
        super.update(browserWidth);
        System.out.println("nav browserWidth: " + browserWidth);
        // Toggle the visibility of the drawer toggle and navbar tabs layout elements based on the browser width
        if (browserWidth < BROWSER_WIDTH_THRESHOLD) { // Use the constant instead of the magic number
            System.out.println("in mobile view");
            drawerToggle.setVisible(true);
            navbarTabsLayout.setVisible(false);
        } else {
            System.out.println("in win view");
            drawerToggle.setVisible(false);
            navbarTabsLayout.setVisible(true);
            if (appLayout.isDrawerOpened()) {
                appLayout.setDrawerOpened(false);
            }
        }
    }
}
