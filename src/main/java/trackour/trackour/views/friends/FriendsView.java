package trackour.trackour.views.friends;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

@Route("friends")
@RouteAlias("friends")
@PageTitle("Friends | Trackour")
// @PreserveOnRefresh
@RolesAllowed({"ADMIN", "USER"})
public class FriendsView extends VerticalLayout {
    SecurityViewService securityViewHandler;
    SecurityViewService securityService;
    CustomUserDetailsService customUserDetailsService;

    Grid<User> currentFriendsGrid = new Grid<>(User.class, false);
    Grid<User> friendRequestGrid = new Grid<>(User.class, false);

    User currentUser;
    User friend;

    public FriendsView(SecurityViewService securityViewHandler, SecurityViewService securityService,
        CustomUserDetailsService customUserDetailsService) {

            this.securityViewHandler = securityViewHandler;
            this.securityService = securityService;
            this.customUserDetailsService = customUserDetailsService;

            this.currentUser = customUserDetailsService.getByUsername(securityService.getAuthenticatedRequestSession().getUsername()).get();

            H3 friendRequestTitle = new H3("Add a new friend");
            TextField friendRequestInput = new TextField("Enter Username");

            Span confirmationText = new Span("");

            Button friendRequestButton = new Button("Submit", e -> sendFriendRequest(friendRequestInput.getValue(), confirmationText));
            friendRequestButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            H3 pendingFriendRequest = new H3("Incoming friend requests");

            H3 currentFriendsTitle = new H3("Your current friends");

            configureRequestGrid();
            configureFriendsGrid();

            addClassName("friends-view");
            // setSizeFull();

            // HorizontalLayout layout = new HorizontalLayout();
            FlexLayout layout = new FlexLayout();
            // layout.getStyle().setBackground("red");
            layout.setFlexGrow(1);
            layout.setHeightFull();
            // categoryLayout.setSizeFull();
            layout.getStyle().set("display", "flex");
            layout.getStyle().set("flex-wrap", "wrap");

            // Set the flex direction to row (horizontal)
            layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);

            // // Set the flex wrap to wrap (vertical when needed)
            layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

            // You can also set the alignment and justify content properties as you like
            // layout.setAlignItems(FlexLayout.Alignment.CENTER);
            // layout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
            

            VerticalLayout friendRequestLayout = new VerticalLayout();
            VerticalLayout currentFriendsLayout = new VerticalLayout();
            // friendRequestLayout.setWidth("50%");
            // friendRequestLayout.getStyle().setBackground("cyan");

            // currentFriendsLayout.setWidth("50%");
            // currentFriendsLayout.getStyle().setBackground("cyan");

            FormLayout friendRequestForm = new FormLayout();

            // friendRequestForm.setSizeFull();
            friendRequestForm.add(friendRequestInput, friendRequestButton, confirmationText);

            // friendRequestLayout.setSizeFull();
            friendRequestLayout.add(friendRequestTitle, friendRequestForm,
                                        pendingFriendRequest, friendRequestGrid);
// 
            // currentFriendsLayout.setSizeFull();
            currentFriendsLayout.add(currentFriendsTitle, currentFriendsGrid);

            layout.add(friendRequestLayout, currentFriendsLayout);

            // Create a responsive navbar component
            NavBar navbar = new NavBar(customUserDetailsService, securityViewHandler);
            // Add some content below the navbar
            navbar.setContent(layout);
            // Add it to the view
            add(navbar);
            

            //User currentUser = customUserDetailsService.getByUsername(securityService.getAuthenticatedUser().getUsername()).get();
            /*
            List<Long> friends = new ArrayList<Long>();
            
            friends.add((long) 100);
            friends.add((long) 200);
            friends.add((long) 300);

            currentUser.setFriendRequests(friends);

            customUserDetailsService.update(currentUser);

            currentUser = customUserDetailsService.getByUsername(securityService.getAuthenticatedUser().getUsername()).get();
            */
            if(!currentUser.getFriendRequests().isEmpty()) {
                List<String> requestUsernames = currentUser.getPendingFriendRequests().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
                System.out.println("Friend requests: " + Arrays.toString(requestUsernames.toArray()));
            }
            if(!currentUser.getFriendsWith().isEmpty()) {
                List<String> friendsUsernames = currentUser.getPendingFriendRequests().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
                System.out.println("Friends: " + Arrays.toString(friendsUsernames.toArray()));
            }
    }

    //friend request grid
    private void configureRequestGrid() {

        this.friendRequestGrid.addClassNames("friend-requests-grid");
        this.friendRequestGrid.addColumn(User::getUsername).setHeader("Username");
        this.friendRequestGrid.addComponentColumn((ev) -> addFriendButton(ev));
        this.friendRequestGrid.addComponentColumn((ev) -> deleteFriendRequestButton(ev));
        this.friendRequestGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.friendRequestGrid.setItems(findYourFriendRequestsList());
    }

    //friends grid
    private void configureFriendsGrid() {
        // get the list of usernames from the list of users
        this.currentFriendsGrid.addClassNames("friends-grid");
        this.currentFriendsGrid.addColumn(User::getUsername).setHeader("Username");
        this.currentFriendsGrid.addComponentColumn((ev) -> deleteFriendButton(ev));
        this.currentFriendsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.currentFriendsGrid.setItems(findYourFriends());
    }

    private void sendFriendRequest(String username, Span text) {
        System.out.println("Find username[for request]: " + username);
        // send friend request to that user
        Optional<User> friendIsPresent = customUserDetailsService.getByUsername(username);
        
        if (friendIsPresent.isPresent()) {
            System.out.println("USER EXISTS!!");
            this.friend = friendIsPresent.get();
            FriendRequestEnum requestResult = customUserDetailsService.sendFriendRequestToThatUser(currentUser, friend);
            refreshGrids();
            
            if(requestResult.equals(FriendRequestEnum.CANNOT_ADD_YOURSELF)) {
                text.setText("You cannot add yourself as a friend!");
                return;
            }
    
            //check if user has already sent a request, or is already friends with the user
            if (requestResult.equals(FriendRequestEnum.REQUEST_ALREADY_SENT)){
                text.setText("Request already sent to " + username + "!");
                return;
            }
    
            // check if already friends
            if (requestResult.equals(FriendRequestEnum.ALREADY_FRIENDS)) {
                text.setText("You are already friends with " + username + "!");
                return;
            }
            text.setText("Friend request sent!");
            return;   
        }
        refreshGrids();
        // this.getUI().get().getPage().reload();
        text.setText("User with username \"" + username + "\" doesn't exist!");
        return;
    }

    private List<User> findYourFriendRequestsList() {
        List<User> friendRequests = new ArrayList<User>();

        if(customUserDetailsService.getAllFriendRequests(currentUser) != null) {
            for (User request : customUserDetailsService.getAllFriendRequests(currentUser)) {
                if(customUserDetailsService.getByUid(request.getUid()).isPresent()) {
                    friendRequests.add(customUserDetailsService.getByUid(request.getUid()).get());
                }
            } 
        }
        return friendRequests;
    }

    private Button deleteFriendRequestButton(User user) {
        // reject friend request (removes that user from your friends request list)
        Icon icon = new Icon("lumo","cross");
        Button button = new Button(icon, (ev) -> {
            System.out.println("REJECT!");
            customUserDetailsService.deleteUserFromYourFriendsRequests(currentUser, user);
            refreshGrids();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private Button addFriendButton(User newFriend) {
        // accept friend request and create friendship
        Icon icon = new Icon("lumo","checkmark");
        Button button = new Button(icon, (ev) -> {
            System.out.println("BEFRIEND!");
            customUserDetailsService.addUserToYourFriendsList(currentUser, newFriend);
            refreshGrids();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private List<User> findYourFriends() {
        return customUserDetailsService.getAllFriends(currentUser);
    }

    private Button deleteFriendButton(User friend) {
        // unfriend
        Icon icon = new Icon("lumo","cross");
        Button button = new Button(icon, (ev) -> {
            System.out.println("UNFRIEND!");
            customUserDetailsService.deleteUserFromYourFriendsList(currentUser, friend);
            refreshGrids();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    public void refreshGrids() {
        ListDataProvider<User> dataProvider2 = new ListDataProvider<>(findYourFriends());
        currentFriendsGrid.setDataProvider(dataProvider2);
        dataProvider2.refreshAll();
        
        ListDataProvider<User> dataProvider1 = new ListDataProvider<>(findYourFriendRequestsList());
        friendRequestGrid.setDataProvider(dataProvider1);
        dataProvider1.refreshAll();
    }

    // @Override
    // public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    //     // this method call reroutes get requests to this view if the current session is already authenticated
    //     // getUI().get().getPage().addJavaScript("window.location.href = 'myurl'");
    //     this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, false);
    // }
}
