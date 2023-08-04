package trackour.trackour.model.task;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    Optional<Task> findById(Long id);
    Optional<Task> findByDescription(String description);
    Optional<Task> findByCreatedAt(LocalDateTime createdAt);
    // Optional<Task> findByProject(String project);
    Optional<Task> findByStatus(TaskStatus status);
    void deleteById(Long id);
}
