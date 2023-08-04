package trackour.trackour.model.user;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class FriendshipId implements Serializable {

  private Long user_id;
  private Long friend_id;

  // default constructor
  public FriendshipId() {}

  // parameterized constructor
  public FriendshipId(Long user_id, Long friend_id) {
      this.user_id = user_id;
      this.friend_id = friend_id;
    // if (user_id < friend_id) {
    //   this.user_id = user_id;
    //   this.friend_id = friend_id;
    // }
    // else {
    //   this.user_id = friend_id;
    //   this.friend_id = user_id;
    // }
  }

  // getters and setters
  public Long getUser_id() {
    return user_id;
  }

  public void setUser_id(Long user_id) {
    this.user_id = user_id;
  }

  public Long getFriend_id() {
    return friend_id;
  }

  public void setFriend_id(Long friend_id) {
    this.friend_id = friend_id;
  }

  // equals and hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FriendshipId that = (FriendshipId) o;
    return Objects.equals(user_id, that.user_id) && Objects.equals(friend_id, that.friend_id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user_id, friend_id);
  }
}

