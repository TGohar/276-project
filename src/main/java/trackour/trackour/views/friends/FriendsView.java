package trackour.trackour.views.friends;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.user.CustomUserDetailsService;
import trackour.trackour.model.user.FriendRequestEnum;
import trackour.trackour.model.user.FriendshipService;
import trackour.trackour.model.user.User;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

@Route("friends")
@RouteAlias("friends")
@PageTitle("Friends | Trackour")
@PreserveOnRefresh
@RolesAllowed({"ADMIN", "USER"})
public class FriendsView extends VerticalLayout {

    @Autowired
    SecurityViewService securityViewService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    FriendshipService friendshipService;

    Grid<User> currentFriendsGrid = new Grid<>(User.class, false);
    Grid<User> friendRequestGrid = new Grid<>(User.class, false);

    User currentUser;
    User friend;

    public FriendsView(
        SecurityViewService securityViewService,
        CustomUserDetailsService customUserDetailsService,
        FriendshipService friendshipService
        ) {

            this.securityViewService = securityViewService;
            this.customUserDetailsService = customUserDetailsService;
            this.friendshipService = friendshipService;

            this.currentUser = customUserDetailsService.getByUsername(securityViewService.getAuthenticatedRequestSession().getUsername()).get();
            
            Span confirmationText = new Span("");

            H3 friendRequestTitle = new H3("Add a new friend");
            TextField friendRequestInput = new TextField("Enter Username");
            friendRequestInput.addKeyUpListener(Key.ENTER, ev -> sendFriendRequest(friendRequestInput.getValue(), confirmationText));

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
            NavBar navbar = new NavBar(customUserDetailsService, securityViewService);
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
            if(findRequests(currentUser) != null) {
                System.out.println("Friend requests: " + String.join(", ", friendshipService.getFriendRequestsUsernames(currentUser.getUid())));
            }
            if(findFriends(currentUser) != null) {
                System.out.println("Friends: " + String.join(", ", friendshipService.getFriendsUsernames(currentUser.getUid())));
            }
    }

    private List<User> findFriends(User user) {
        return friendshipService.getFriends(user.getUid());
    }

    private List<User> findRequests(User user) {
        return friendshipService.getFriendRequests(user.getUid());
    }

    private void refreshRequestGrid() {
        this.friendRequestGrid.setItems(findRequests(currentUser));
        friendRequestGrid.getDataProvider().refreshAll();
    }

    private void refreshFriendsGrid() {
        this.currentFriendsGrid.setItems(findFriends(currentUser));
        currentFriendsGrid.getDataProvider().refreshAll();
    }

    //friend request grid
    private void configureRequestGrid() {
        this.friendRequestGrid.addClassNames("friend-requests-grid");
        // this.friendRequestGrid.setSizeFull();
        this.friendRequestGrid.addColumn(User::getUsername).setHeader("Username");
        this.friendRequestGrid.addComponentColumn((ev) -> accept(ev));
        this.friendRequestGrid.addComponentColumn((ev) -> reject(ev));
        this.friendRequestGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        refreshRequestGrid();
    }

    //friends grid
    private void configureFriendsGrid() {
        this.currentFriendsGrid.addClassNames("friends-grid");
        // this.currentFriendsGrid.setSizeFull();
        this.currentFriendsGrid.addColumn(User::getUsername).setHeader("Username");
        this.currentFriendsGrid.addComponentColumn((ev) -> unfriend(ev));
        this.currentFriendsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        refreshFriendsGrid();
    }

    private void sendFriendRequest(String username, Span text) {

        Optional<User> optionFriend = customUserDetailsService.getByUsername(username); 
        if(optionFriend.isPresent()) {
            this.friend = optionFriend.get();
            FriendRequestEnum friendRequestSubmittedRespone = friendshipService.sendFriendRequest(friend, currentUser);
            //Check if friend user exists
            if (friendRequestSubmittedRespone.equals(FriendRequestEnum.USER_DOES_NOT_EXIST)) {
                text.setText("Unable to find " + username + "!");
                return;
            }
            else if(friendRequestSubmittedRespone.equals(FriendRequestEnum.CANNOT_ADD_YOURSELF)) {
                text.setText("You cannot add yourself as a friend!");
                return;
            }
            //check if user has already sent a request, or is already friends with the user
            else if (friendRequestSubmittedRespone.equals(FriendRequestEnum.REQUEST_ALREADY_SENT)){
                text.setText("Request already sent to " + username + "!");
                return;
            }
            else if (friendRequestSubmittedRespone.equals(FriendRequestEnum.ALREADY_FRIENDS)) {
                text.setText("You are already friends with " + username + "!");
                return;
            }
            else if (friendRequestSubmittedRespone.equals(FriendRequestEnum.SUCCESS)) {
                text.setText("Friend request sent!");
                return;
            }
            text.setText("Unknown action. Please try a different action.");
        }
        text.setText("Unable to find " + username + "!");
        return;
    }

    private Button reject(User user) {
        Icon icon = new Icon("lumo","cross");
        Span buttonText = new Span("Reject");
        buttonText.add(icon);
        Button button = new Button(buttonText, (ev) -> {
            friendshipService.rejectFriendRequest(currentUser, user);
            refreshRequestGrid();
            refreshFriendsGrid();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private Button accept(User newFriend) {
        Icon icon = new Icon("lumo","checkmark");
        Span buttonText = new Span("Accept");
        buttonText.add(icon);
        Button button = new Button(buttonText, (ev) -> {
            friendshipService.acceptFriendRequest(currentUser, newFriend);
            refreshRequestGrid();
            refreshFriendsGrid();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private Button unfriend(User friend) {
        Icon icon = new Icon("lumo","cross");
        Span buttonText = new Span("Unfriend");
        buttonText.add(icon);
        Button button = new Button(buttonText, (ev) -> {
            friendshipService.unfriend(currentUser.getUid(), friend.getUid());
            refreshRequestGrid();
            refreshFriendsGrid();
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    // @Override
    // public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    //     // this method call reroutes get requests to this view if the current session is already authenticated
    //     // getUI().get().getPage().addJavaScript("window.location.href = 'myurl'");
    //     this.securityViewHandler.handleAnonymousOnly(beforeEnterEvent, false);
    // }
}
