package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findById(Long id);
    Optional<Project> findByTitle(String title);
    Optional<Project> findByCreatedAt(LocalDateTime createdAt);
    Optional<Project> findByStatus(ProjectStatus status);
    void deleteById(Long id);
}
