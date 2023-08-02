package trackour.trackour.model.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(Long uid);
    Optional<User> findByUsername(String username);
    Optional<User> findByDisplayName(String displayName);
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    void deleteByUid(Long uid);
}
