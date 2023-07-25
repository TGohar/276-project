package trackour.trackour.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findById(String id);
    Optional<Project> findByTitle(String title);
    Optional<Project> findByDescription(String description);
    Optional<Project> findByCreatedAt(LocalDateTime createdAt);
    Optional<Project> findByOwner(Long owner);
    Optional<Project> findByStatus(ProjectStatus status);
    void deleteById(String id);
}
