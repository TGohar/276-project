package trackour.trackour.views.home;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.friends.FriendsView;

public class NavBar {
    SecurityViewService securityViewHandler;
    CustomUserDetailsService customUserDetailsService;
    static String searchValue;

    public NavBar(CustomUserDetailsService customUserDetailsService, SecurityViewService securityViewHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityViewHandler = securityViewHandler;
    }

    public VerticalLayout generateNavBar() {
        H1 header = new H1("Trackour");

        RouteTabs routeTabs = new RouteTabs();

        routeTabs.add(new RouterLink("HOME", HomeView.class));
        routeTabs.add(new RouterLink("FRIENDS", FriendsView.class));

        String sessionUsername = securityViewHandler.getAuthenticatedRequestSession().getUsername();
        // since logged in, no need to verify if this optional is empty
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();
        Text displayNameTxt = new Text("@" + displayNameString);
        Button signUpButton = new Button("Sign Up");
        signUpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        signUpButton.addClassName("button-hover-effect");
        signUpButton.addClickListener(event -> {
            UI.getCurrent().navigate("signUp");
        });

        Button LoginButton = new Button("Logout");
        LoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        LoginButton.addClassName("button-hover-effect");
        LoginButton.addClickListener(event -> {
            securityViewHandler.logOut();
        });

        Button exploreButton = new Button("Explore");
        exploreButton.addClickListener(e -> exploreButton.getUI().ifPresent(ui -> ui.navigate("Explore")));

        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.setPlaceholder("Music language");
        languageComboBox.setItems("English", "Punjabi", "Spanish", "French", "German", "Hindi");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Songs, Albums, Artists");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.addValueChangeListener(e -> {

                searchValue=e.getValue();

                if(searchValue!=null && searchValue.length()!=0){
                    searchField.getUI().ifPresent(ui -> ui.navigate("searchResult"));
                }
                else{
                   Notification.show("Please enter the name of the song, album or artist you want to search"); 
                }
                });

        Icon mediaIcon = new Icon(VaadinIcon.MUSIC);
        Button mediaShelfButton = new Button("Media Shelf", mediaIcon);
        HorizontalLayout topNavButtons = new HorizontalLayout(displayNameTxt, LoginButton);
        topNavButtons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        topNavButtons.getStyle().set("gap", "10px"); // Add spacing between the buttons

        HorizontalLayout graphics = new HorizontalLayout();
        graphics.add(header, exploreButton);
        graphics.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Create a layout for the header and buttons
        HorizontalLayout topNavBar = new HorizontalLayout(graphics, searchField, languageComboBox, mediaShelfButton,
                topNavButtons);
        topNavBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topNavBar.setWidthFull();
        // topNavBar.expand(exploreButton);
        topNavBar.expand(header);
        topNavBar.expand(searchField);
        topNavBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout layout = new VerticalLayout();

        layout.setWidthFull();

        layout.add(topNavBar, routeTabs);

        return layout;
    }

    private static class RouteTabs extends Tabs implements BeforeEnterObserver {
        private final Map<RouterLink, Tab> routerLinkTabMap = new HashMap<>();

        public void add(RouterLink routerLink) {
            routerLink.setHighlightCondition(HighlightConditions.sameLocation());
            routerLink.setHighlightAction(
                    (link, shouldHighlight) -> {
                        if (shouldHighlight)
                            setSelectedTab(routerLinkTabMap.get(routerLink));
                    });
            routerLinkTabMap.put(routerLink, new Tab(routerLink));
            addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
            setWidthFull();
            add(routerLinkTabMap.get(routerLink));
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
            // In case no tabs will match
            setSelectedTab(null);
        }
    }

    public static String getSearchValue(){
            return searchValue;
        } 
}
