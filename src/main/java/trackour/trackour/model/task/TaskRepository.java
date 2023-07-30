package trackour.trackour.model.task;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trackour.trackour.model.project.Project;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    Optional<Task> findById(String id);
    Optional<Task> findByDescription(String description);
    Optional<Task> findByCreatedAt(LocalDateTime createdAt);
    Optional<Task> findByProject(Project project);
    Optional<Task> findByStatus(TaskStatus status);
    void deleteById(String id);
}
