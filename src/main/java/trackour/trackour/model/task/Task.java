package trackour.trackour.model.task;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import trackour.trackour.model.project.Project;

@Entity
@Table( name="tasks",   uniqueConstraints= @UniqueConstraint(columnNames={"task_id"}) )
public class Task {

    @Id
    @Column(name = "task_id", length = 36, nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "task_title")
    private String title;

    @Column(name = "task_description")
    private String description;

    @Column(name = "task_status")
    private TaskStatus status;
    
    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    private Set<Long> assignees;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // ----------methods------------------------------------------
    public Task(Project project) {
        this.title = "New Task";
        this.description = "Placeholder description.";
        this.status = TaskStatus.NOT_STARTED;
        this.project = project;
    }
    
    public Set<Long> getAssignees() {
        return assignees;
    }
    
    public void setAssignees(Set<Long> assignees) {
        this.assignees = assignees;
    }

    public Project getProject() {
        return this.project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }    

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}