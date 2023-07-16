package trackour.trackour.views.friends;
import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import jakarta.annotation.security.RolesAllowed;

import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
import trackour.trackour.security.SecurityService;
import trackour.trackour.security.SecurityViewHandler;
import trackour.trackour.views.home.NavBar;

@Route("friends")
@PageTitle("Friends")
@RolesAllowed({"ADMIN", "USER"})
public class FriendsView extends VerticalLayout{
    SecurityViewHandler securityViewHandler;
    SecurityService securityService;
    CustomUserDetailsService customUserDetailsService;

    Grid<User> currentFriendsGrid = new Grid<>(User.class, false);
    Grid<User> friendRequestGrid = new Grid<>(User.class, false);

    User currentUser;
    User friend;

    public FriendsView(SecurityViewHandler securityViewHandler, SecurityService securityService,
        CustomUserDetailsService customUserDetailsService) {

            this.securityViewHandler = securityViewHandler;
            this.securityService = securityService;
            this.customUserDetailsService = customUserDetailsService;

            this.currentUser = customUserDetailsService.getByUsername(securityService.getAuthenticatedUser().getUsername()).get();

            NavBar navigation = new NavBar(securityService, customUserDetailsService, securityViewHandler);

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
            setSizeFull();

            HorizontalLayout layout = new HorizontalLayout();

            VerticalLayout friendRequestLayout = new VerticalLayout();
            VerticalLayout currentFriendsLayout = new VerticalLayout();

            FormLayout friendRequestForm = new FormLayout();

            friendRequestForm.setSizeFull();
            friendRequestForm.add(friendRequestInput, friendRequestButton, confirmationText);

            friendRequestLayout.setSizeFull();
            friendRequestLayout.add(friendRequestTitle, friendRequestForm,
                                        pendingFriendRequest, friendRequestGrid);

            currentFriendsLayout.setSizeFull();
            currentFriendsLayout.add(currentFriendsTitle, currentFriendsGrid);


            layout.setSizeFull();
            layout.add(friendRequestLayout, currentFriendsLayout);

            add(navigation.generateNavBar(), layout);
            

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
            if(currentUser.getFriendRequests() != null) {
                System.out.println("Friend requests: " + Arrays.toString(currentUser.getFriendRequests().toArray()));
            }
            if(currentUser.getFriends() != null) {
                System.out.println("Friends: " + Arrays.toString(currentUser.getFriends().toArray()));
            }
    }

    private void configureRequestGrid() {
        this.friendRequestGrid.addClassNames("friend-requests-grid");
        this.friendRequestGrid.setSizeFull();
        this.friendRequestGrid.addColumn(User::getUsername).setHeader("Username");
        this.friendRequestGrid.addComponentColumn((ev) -> addFriendButton(ev));
        this.friendRequestGrid.addComponentColumn((ev) -> deleteFriendRequestButton(ev));
        this.friendRequestGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.friendRequestGrid.setItems(findRequests());
    }

    private void configureFriendsGrid() {
        this.currentFriendsGrid.addClassNames("friends-grid");
        this.currentFriendsGrid.setSizeFull();
        this.currentFriendsGrid.addColumn(User::getUsername).setHeader("Username");
        this.currentFriendsGrid.addComponentColumn((ev) -> deleteFriendButton(ev));
        this.currentFriendsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.currentFriendsGrid.setItems(findFriends());
    }

    private void sendFriendRequest(String username, Span text) {
        //Check if friend user exists
        if (!customUserDetailsService.getByUsername(username).isPresent()) {
            text.setText("Unable to find " + username + "!");
            return;
        }

        this.friend = customUserDetailsService.getByUsername(username).get();

        if(friend.getUid() == currentUser.getUid()) {
            text.setText("You cannot add yourself as a friend!");
            return;
        }

        List<Long> friendRequests = this.friend.getFriendRequests();
        List<Long> friends = this.currentUser.getFriends();

        //check if user has already sent a request, or is already friends with the user
        if(friendRequests != null) {
            for(Long requests : friendRequests) {
                if(requests == this.currentUser.getUid()) {
                    text.setText("Request already sent to " + username + "!");
                    return;
                }
            }
            if(currentUser.getFriends() != null){
                for(Long currentFriend : friends) {
                    if(currentFriend == this.friend.getUid()) {
                        text.setText("You are already friends with " + username + "!");
                        return;
                    }
                }
            }
        }
        
        //add new friend request to friend's list and update DB
        if(friendRequests == null) {
            friendRequests = new ArrayList<Long>();
        }

        friendRequests.add(this.currentUser.getUid());
        friend.setFriendRequests(friendRequests);

        customUserDetailsService.updateUser(friend);

        text.setText("Friend request sent!");
    }

    private List<User> findRequests() {
        List<User> friendRequests = new ArrayList<User>();

        if(currentUser.getFriendRequests() != null) {
            for (Long request : currentUser.getFriendRequests()) {
                if(customUserDetailsService.get(request).isPresent()) {
                    friendRequests.add(customUserDetailsService.get(request).get());
                }
            } 
        }
        return friendRequests;
    }

    private Button deleteFriendRequestButton(User user) {
        Icon icon = new Icon("lumo","cross");
        Button button = new Button(icon, (ev) -> {
            List<Long> requests = this.currentUser.getFriendRequests();
            requests.remove(user.getUid());
            this.currentUser.setFriendRequests(requests);
            this.customUserDetailsService.updateUser(currentUser);
            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private Button addFriendButton(User newFriend) {
        Icon icon = new Icon("lumo","checkmark");
        Button button = new Button(icon, (ev) -> {
            List<Long> requests = this.currentUser.getFriendRequests();
            List<Long> friends = this.currentUser.getFriends();
            if (friends == null) {
                friends = new ArrayList<Long>();
            }

            List<Long> newFriendFriends = newFriend.getFriends();
            if (newFriendFriends == null) {
                newFriendFriends = new ArrayList<Long>();
            }

            friends.add(newFriend.getUid());
            requests.remove(newFriend.getUid());

            newFriendFriends.add(this.currentUser.getUid());

            this.currentUser.setFriends(friends);
            this.currentUser.setFriendRequests(requests);

            newFriend.setFriends(newFriendFriends);

            customUserDetailsService.updateUser(currentUser);
            customUserDetailsService.updateUser(newFriend);

            this.getUI().get().getPage().reload();
        });

        return button;
    }

    private List<User> findFriends() {
        List<User> friends = new ArrayList<User>();

        if(currentUser.getFriends() != null) {
            for (Long friend : currentUser.getFriends()) {
                if(customUserDetailsService.get(friend).isPresent()) {
                    friends.add(customUserDetailsService.get(friend).get());
                }
            } 
        }
        return friends;
    }

    private Button deleteFriendButton(User friend) {
        Icon icon = new Icon("lumo","cross");
        Button button = new Button(icon, (ev) -> {
            List<Long> friends = this.currentUser.getFriends();
            friends.remove(friend.getUid());

            List<Long> friendFriends = friend.getFriends();
            friendFriends.remove(this.currentUser.getUid());

            this.currentUser.setFriends(friends);
            this.customUserDetailsService.updateUser(currentUser);

            friend.setFriends(friendFriends);
            this.customUserDetailsService.updateUser(friend);
            this.getUI().get().getPage().reload();
        });

        return button;
    }
}
