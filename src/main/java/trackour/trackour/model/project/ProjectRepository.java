package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trackour.trackour.model.user.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findById(String id);
    Optional<Project> findByTitle(String title);
    Optional<Project> findByCreatedAt(LocalDateTime createdAt);
    Optional<Project> findByOwner(User owner);
    Optional<Project> findByStatus(ProjectStatus status);
    void deleteById(String id);
}
