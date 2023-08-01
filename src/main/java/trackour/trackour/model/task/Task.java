package trackour.trackour.model.task;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import trackour.trackour.model.project.Project;
import trackour.trackour.model.user.User;

@Entity
@Table( name="tasks",   uniqueConstraints= @UniqueConstraint(columnNames={"task_id"}) )
public class Task {

    @Id
    @Column(name = "task_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // a task can have many assignees
    @ManyToMany
    @JoinTable(
        name = "task_assignees", // name of the join table
        joinColumns = @JoinColumn(name = "task_id"), // foreign key column for Task
        inverseJoinColumns = @JoinColumn(name = "user_id") // foreign key column for User
    )
    private Set<User> assignees;

    @Column(name = "task_title")
    private String title;

    @Column(name = "task_description")
    private String description;

    @Column(name = "task_status")
    private TaskStatus status;
    
    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // ----------methods------------------------------------------
    public Task(Project project) {
        this.title = "New Task";
        this.description = "Placeholder description.";
        this.status = TaskStatus.NOT_STARTED;
        this.project = project;
        initCollections();
    }

    private void initCollections() {
        assignees = new HashSet<>();
    }
    
    public Set<User> getAssignees() {
        return assignees;
    }
    
    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

    public Project getProject() {
        return this.project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }    

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
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