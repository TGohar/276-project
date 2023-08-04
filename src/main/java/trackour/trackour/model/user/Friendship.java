package trackour.trackour.model.user;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class Friendship {
  // composite id consisting of user_id and friend_id

  @EmbeddedId
  private FriendshipId id;

  @ManyToOne
  @MapsId("user_id")
  private User user;

  @ManyToOne
  @MapsId("friend_id")
  private User friend;

  @Enumerated(EnumType.STRING)
  private Status status;

  public enum Status {
    PENDING, // friend request sent but not accepted
    ACCEPTED, // friend request accepted
    REJECTED, // friend request rejected
    BLOCKED // user blocked by another user
  }
  
  public Friendship() {
  }

  // parameterized constructor
  public Friendship(FriendshipId id, User user, User friend, Friendship.Status status) {
    this.id = id;
    this.user = user;
    this.friend = friend;
    this.status = status;
  }
  

  // getters and setters
public FriendshipId getId() {
    return id;
  }
  
  public void setId(FriendshipId id) {
    this.id = id;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public User getFriend() {
    return friend;
  }
  
  public void setFriend(User friend) {
    this.friend = friend;
  }
  
  public Status getStatus() {
    return status;
  }
  
  public void setStatus(Status status) {
    this.status = status;
  }
  
}