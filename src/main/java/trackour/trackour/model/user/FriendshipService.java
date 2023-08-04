package trackour.trackour.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class FriendshipService {

  @Autowired
  private FriendshipRepository friendshipRepository;

  @Autowired
  CustomUserDetailsService customUserDetailsService;
  
  // Define constants for messages
  private static final String ADDING_FRIEND_MESSAGE = "Adding %s as a new friend";
  private static final String REJECT_FRIEND_REQUEST_MESSAGE = "Reject %s from your friend requests";
  private static final String REMOVE_FRIEND_MESSAGE = "Remove %s from your friend list";
  
  public List<Long> getFriendsUids(Long userId) {
      List<Long> usernames = new ArrayList<>();
      if (userId == null) {
          return usernames;
        }
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(new User(userId), Friendship.Status.ACCEPTED);
        for (Friendship friendship : friendships) {
          usernames.add(friendship.getFriend().getUid());
        }
        return usernames;
    }
  public List<String> getFriendsUsernames(Long userId) {
      List<String> usernames = new ArrayList<>();
      if (userId == null) {
          return usernames;
        }
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(new User(userId), Friendship.Status.ACCEPTED);
        for (Friendship friendship : friendships) {
          usernames.add(friendship.getFriend().getUsername());
        }
        return usernames;
    }
  
  public List<String> getFriendRequestsUsernames(Long userId) {
      List<String> usernames = new ArrayList<>();
      if (userId == null) {
          return usernames;
        }
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(new User(userId), Friendship.Status.PENDING);
        for (Friendship friendship : friendships) {
          usernames.add(friendship.getFriend().getUsername());
        }
        return usernames;
    }

    public List<User> getFriends(Long userId) {
      List<User> users = new ArrayList<>();
      if (userId == null) {
          return users;
        }
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(new User(userId), Friendship.Status.ACCEPTED);
        for (Friendship friendship : friendships) {
          users.add(friendship.getFriend());
        }
        return users;
    }
    public List<User> getFriendRequests(Long userId) {
      List<User> users = new ArrayList<>();
      if (userId == null) {
          return users;
        }
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(new User(userId), Friendship.Status.PENDING);
        for (Friendship friendship : friendships) {
          users.add(friendship.getFriend());
        }
        return users;
    }

  public boolean isFriend(Long userId, Long friendId) {
    FriendshipId id = new FriendshipId(userId, friendId);
    Optional<Friendship> friendship = friendshipRepository.findById(id);
    return friendship.isPresent() && friendship.get().getStatus() == Friendship.Status.ACCEPTED;
  }

  public void unfriend(Long userId, Long friendId) {
    FriendshipId id = new FriendshipId(userId, friendId);
    friendshipRepository.deleteById(id);
    FriendshipId id2 = new FriendshipId(friendId, userId);
    friendshipRepository.deleteById(id2);
    System.out.println(REMOVE_FRIEND_MESSAGE);
  }

  /**
 * Sends a friend request from one user to another user.
 * @param user the user who sends the request
 * @param friend the user who receives the request
 * @return a FriendRequestEnum value indicating the result of the request
 */
public FriendRequestEnum sendFriendRequest(User user, User friend) {
  // check if user or friend is null
  if (user == null || friend == null) {
    return FriendRequestEnum.USER_DOES_NOT_EXIST;
  }
  // check if user and friend are the same
  if (user.getUid().equals(friend.getUid())) {
    return FriendRequestEnum.CANNOT_ADD_YOURSELF;
  }
  // check if friendship already exists
  FriendshipId id = new FriendshipId(user.getUid(), friend.getUid());
  Optional<Friendship> existingFriendship = friendshipRepository.findById(id);
  if (existingFriendship.isPresent()) {
    // check if friendship is pending or accepted
    Friendship.Status status = existingFriendship.get().getStatus();
    if (status == Friendship.Status.PENDING) {
      return FriendRequestEnum.REQUEST_ALREADY_SENT;
    } else if (status == Friendship.Status.ACCEPTED) {
      return FriendRequestEnum.ALREADY_FRIENDS;
    }
  }
  // create and save a new friendship with pending status
  try {
    Friendship friendship = new Friendship(id, user, friend, Friendship.Status.PENDING);
    friendshipRepository.save(friendship);
    System.out.println(String.format(ADDING_FRIEND_MESSAGE, friend.getUsername()));
    return FriendRequestEnum.SUCCESS;
  } catch (Exception e) {
    // handle any exceptions here
    e.printStackTrace();
    return FriendRequestEnum.USER_DOES_NOT_EXIST;
  }
}

  public void acceptFriendRequest(User user, User friend) {
    
    // check if the friendship already exists
    FriendshipId id = new FriendshipId(user.getUid(), friend.getUid());
    Optional<Friendship> existingFriendship = friendshipRepository.findById(id);
    if (existingFriendship.isPresent()) {
      Friendship requestedFriendship = existingFriendship.get();
      System.out.println("requestedFriendship: " + requestedFriendship.getId().getFriend_id() + " sent to " + requestedFriendship.getId().getUser_id() + " " + requestedFriendship.getStatus().name());
      // check if friendship is pending or accepted
      Friendship.Status status = existingFriendship.get().getStatus();
      if (status == Friendship.Status.PENDING) {
        // request exists and is PENDING
        // then accept
        requestedFriendship.setStatus(Friendship.Status.ACCEPTED);
        FriendshipId idAccept = new FriendshipId(friend.getUid(), user.getUid());
        Friendship acceptedFriendship = new Friendship(idAccept, friend, user, Friendship.Status.ACCEPTED);
        System.out.println("acceptedFriendship: " + acceptedFriendship.getId().getUser_id() + " sent to " + acceptedFriendship.getId().getFriend_id() + " " + acceptedFriendship.getStatus().name());
        friendshipRepository.save(requestedFriendship);
        friendshipRepository.save(acceptedFriendship);
      }
    }
    System.out.println(ADDING_FRIEND_MESSAGE);
  }

  public void rejectFriendRequest(User user, User friend) {
    FriendshipId id = new FriendshipId(user.getUid(), friend.getUid());
    Friendship friendship = friendshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Friend relationship not found"));
    friendship.setStatus(Friendship.Status.REJECTED);
    friendshipRepository.save(friendship);
    System.out.println(REJECT_FRIEND_REQUEST_MESSAGE);
  }
}