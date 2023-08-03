package trackour.trackour.model.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
  Optional<Friendship> findById(FriendshipId id);
  void deleteById(FriendshipId id);
  List<Friendship> findAllByUserAndStatus(User user, Friendship.Status status);
  List<Friendship> findAllByUserOrFriend(User user, User friend);
}

