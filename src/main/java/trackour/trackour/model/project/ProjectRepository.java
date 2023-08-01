package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trackour.trackour.model.user.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findById(String id);
    Optional<Project> findByTitle(String title);
    Optional<Project> findByCreatedAt(LocalDateTime createdAt);
    Optional<Project> findByStatus(ProjectStatus status);
    List<Project> findByOwner(User owner);
    void deleteById(String id);
}
