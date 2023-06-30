package trackour.trackour.models;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(Long uid);
    Optional<User> findByUsername(String username);
    Optional<User> findByDisplayName(String displayName);
    Optional<User> findByEmail(String email);
    void deleteByUid(Long uid);
}
