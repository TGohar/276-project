package trackour.trackour.model.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
// import java.util.HashSet;
import java.util.Optional;
// import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;
import trackour.trackour.views.friends.FriendRequestEnum;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    // Define constants for messages
    private static final String ADDING_FRIEND_MESSAGE = "Adding %s as a new friend";
    private static final String REJECT_FRIEND_REQUEST_MESSAGE = "Reject %s from your friend requests";
    private static final String REMOVE_FRIEND_MESSAGE = "Remove %s from your friend list";

    public List<User> getAllFriends(User user) {
        if (user == null){
            return Arrays.asList();
        }
        Optional<User> currentUserOptional = userRepository.findByUid(user.getUid());
        if (currentUserOptional.isPresent()) {
            // Use the variable instead of calling the method again
            return currentUserOptional.get().getFriendsWith();
        }
        return Arrays.asList();
    }

    public List<User> getByIds(List<Long> ids) {
        List<User> res = new ArrayList<>();
        for (Long id  : ids) {
            // Use a variable to store the result of the first call
            Optional<User> userOptional = getByUid(id);
            if (userOptional.isPresent()) {
                // Use the variable instead of calling the method again
                res.add(userOptional.get());
            }
        }
        return res;
    }

    public List<User> getAllFriendRequests(User user) {
        if (user == null){
            return Arrays.asList();
        }
        Optional<User> currentUserOptional = userRepository.findByUid(user.getUid());
        if (currentUserOptional.isPresent()) {
            return userRepository.findByUid(user.getUid()).get().getPendingFriendRequests();
        }
        return Arrays.asList();
    }

    public FriendRequestEnum sendFriendRequestToThatUser(User currentUser, User friend) {
        // they are swapped cus 4 soime reason it was populating the sender's/requester/s request list instead of the recepient's.
        // i was fed up and just did this temp fix
        if (friend == null) {
            return FriendRequestEnum.USER_DOES_NOT_EXIST;
        }
        System.out.println("Send " + friend.getUsername() + " a friend req");
        if (friend.getUsername().equals(currentUser.getUsername())) {
            return FriendRequestEnum.CANNOT_ADD_YOURSELF;
        }
        
        List<User> friendRequests = getAllFriendRequests(friend);
        List<User> friends = getAllFriends(currentUser);

        // Use a HashSet to store the uids of the users in the friend requests list
        HashSet<Long> friendRequestsUids = new HashSet<>();
        for (User user : friendRequests) {
            friendRequestsUids.add(user.getUid());
        }

        // Use a HashSet to store the uids of the users in the friends list
        HashSet<Long> friendsUids = new HashSet<>();
        for (User user : friends) {
            friendsUids.add(user.getUid());
        }

        // Use the contains method of the HashSet to check for membership
        if (friendRequestsUids.contains(currentUser.getUid())){
            return FriendRequestEnum.REQUEST_ALREADY_SENT;
        }

        if (friendsUids.contains(friend.getUid())){
            return FriendRequestEnum.ALREADY_FRIENDS;
        }

        friendRequests.add(currentUser);
        friend.setPendingFriendRequests(friendRequests);

        update(friend);
        return FriendRequestEnum.SUCCESS;
    }

    public boolean addUserToYourFriendsList(User currentUser, User newFriend) {
        System.out.println(String.format(ADDING_FRIEND_MESSAGE, newFriend.getUsername()));
        // friends lists
        List<User> requests = getAllFriendRequests(currentUser); // get the list of your friend requests
        List<User> friends = getAllFriends(currentUser); // get the list of your friends
        List<User> otherUserRequests = getAllFriendRequests(newFriend); // get the list of your friend requests
        List<User> newFriendFriends = getAllFriends(newFriend); // get the list of your friends

        
        friends.add(newFriend);

        // Use a HashSet to store the uids of the users in the requests lists
        HashSet<Long> requestsUids = new HashSet<>();
        for (User user : requests) {
            requestsUids.add(user.getUid());
        }

        HashSet<Long> otherUserRequestsUids = new HashSet<>();
        for (User user : otherUserRequests) {
            otherUserRequestsUids.add(user.getUid());
        }

        // Use the remove method of the HashSet to remove a user by uid
        requestsUids.remove(newFriend.getUid());
        otherUserRequestsUids.remove(currentUser.getUid());

        // Convert the HashSet back to a List
        requests = new ArrayList<>();
        for (Long uid : requestsUids) {
            requests.add(getByUid(uid).get());
        }

        otherUserRequests = new ArrayList<>();
        for (Long uid : otherUserRequestsUids) {
            otherUserRequests.add(getByUid(uid).get());
        }

        newFriendFriends.add(currentUser);

        currentUser.setFriendsWith(friends);
        currentUser.setPendingFriendRequests(requests);

        newFriend.setFriendsWith(newFriendFriends);

        update(currentUser);
        update(newFriend);
        return true;
    }

    public boolean deleteUserFromYourFriendsRequests(User currentUser, User user) {
        System.out.println(String.format(REJECT_FRIEND_REQUEST_MESSAGE, user.getUsername()));
        List<User> requests = getAllFriendRequests(currentUser);  // get the list of your friend requests
        
        // Use a HashSet to store the uids of the users in the requests list
        HashSet<Long> requestsUids = new HashSet<>();
        for (User u : requests) {
            requestsUids.add(u.getUid());
        }

        // Use the remove method of the HashSet to remove a user by uid
        requestsUids.remove(user.getUid());

        // Convert the HashSet back to a List
        requests = new ArrayList<>();
        for (Long uid : requestsUids) {
            requests.add(getByUid(uid).get());
        }

        currentUser.setPendingFriendRequests(requests);
        
         // sve your modified data
         update(currentUser);
         return true;
    }

    public boolean deleteUserFromYourFriendsList(User currentUser, User friend) {
        System.out.println(String.format(REMOVE_FRIEND_MESSAGE, friend.getUsername()));
        // friends lists
        List<User> friends = getAllFriends(currentUser);
        friends = friends.stream()
          .filter(u -> !u.getUid().equals(friend.getUid()))
          .collect(Collectors.toList());
        
        List<User> friendFriends = getAllFriends(friend);
        friendFriends = friendFriends.stream()
          .filter(u -> !u.getUid().equals(currentUser.getUid()))
          .collect(Collectors.toList());

        currentUser.setFriendsWith(friends);
        update(currentUser);

        friend.setFriendsWith(friendFriends);
        update(friend);
        
        return true;
    }

    public Optional<User> getByUid(Long uid) {
        return userRepository.findByUid(uid);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getByPasswordResetToken(String passwordResetToken) {
        return userRepository.findByPasswordResetToken(passwordResetToken);
    }

    public User update(User entity) {
        return userRepository.saveAndFlush(entity);
    }

    public void delete(User user) {
        userRepository.deleteByUid(user.getUid());
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public int count() {
        return (int) userRepository.count();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getAuthorities(user.get()));
        }
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        System.out.println(user.getDisplayName() + " has roles:");
        return user.getRoles().stream().map(authority -> {
            System.out.println(authority.roleToString());
            return new SimpleGrantedAuthority(authority.roleToRoleString());
        }).collect(Collectors.toList());
    }
    
    /**
     * Prettyy print {@link User} object
     * @param user
     */
    public void printUserObj(User user) {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(SerializationFeature.INDENT_OUTPUT); //pretty print
        String objStr;
        try {
            objStr = objMapper.writeValueAsString(user);
            System.out.println(objStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * eg. Call {@code passwordEncoder().encode("rawPasscode");} to encrypt "rawPasscode".
     * To just get an instance of the encoder, call {@code passwordEncoder()}
     * @return {@link BCryptPasswordEncoder} encrypted String
     */
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Recieve new user credentials, validates then updates database with this new user,
     * @param username
     * @param password
     */
    public boolean registerUser(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        // unable to properly implement hashing technique atm so will either drop that or tryb again later
        String encodedPassword = passwordEncoder().encode(password);
        newUser.setPassword(encodedPassword);
        printUserObj(newUser);
        return this.submitUser(newUser);
    }

    public boolean registerUser(User newUser) {
        // unable to properly implement hashing technique atm so will either drop that or tryb again later
        String encodedPassword = passwordEncoder().encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        printUserObj(newUser);

        // extra validation
        // if displayName was submitted as empty, use the username string in place of it
        if (newUser.getDisplayName().isEmpty()){
            newUser.setDisplayName(newUser.getUsername());
        }
        return this.submitUser(newUser);
    }

    private boolean submitUser(User newUser) {
        // if user doesn't already exist do new registration
        Optional<User> existingUser = getByUsername(newUser.getUsername());
        if (!existingUser.isPresent()){
            update(newUser);
            return true;
        }   
        return false;
    }

    public boolean updatePassword(User oldUser) {
        Optional<User> existingUser = getByUsername(oldUser.getUsername());
        if(!existingUser.isPresent()){
            return false;
        }

        String encodedPassword = passwordEncoder().encode(oldUser.getPassword());
        oldUser.setPassword(encodedPassword);
        printUserObj(oldUser);

        // invalidate the used token with an empty string
        oldUser.setPasswordResetToken("");

        update(oldUser);

        return true;
    }

    //only use this one if no changes to the password have been made
    public boolean updateUser(User user) {
        Optional<User> existingUser = getByUsername(user.getUsername());
        if(!existingUser.isPresent()){
            return false;
        }

        update(user);
        
        return true;
    }
}
