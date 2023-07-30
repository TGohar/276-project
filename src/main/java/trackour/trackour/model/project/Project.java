package trackour.trackour.model.project;

import java.time.LocalDateTime;
// import java.util.Set;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.user.User;

@Entity
@Table(
    name="projects", 
    uniqueConstraints= @UniqueConstraint(columnNames={"project_id", "owner_id"})
    )
public class Project {
    
    @Id
    @JsonProperty(access = Access.READ_ONLY)
    @Column(name = "project_id", length = 36, nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;


    @Column(name = "title")
    private String title;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    
    // projects can be categorized by status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;
    
    // a project can have many tasks
    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;
    
    // is this project collaborative
    @Column(name = "collaboration_mode")
    private CollaborationMode collaborationMode;
    
    // a project can have one owner
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // a project can have many participants
    @ManyToMany
    @JoinTable(name = "project_participants",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "uid"))
    private Set<User> participants;

    // progress double. can be used by progress bar or charts
    @Column(name = "progress")
    private Double progress;

    public Project() {
        initStatus();
    }

    private void initStatus() {
        this.status = ProjectStatus.IN_PROGRESS;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public Set<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public CollaborationMode getCollaborationMode() {
        return this.collaborationMode;
    }

    public void setCollaborationMode(CollaborationMode collaborationMode) {
        this.collaborationMode = collaborationMode;
    }

    public Double getProgress() {
        return this.progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
    
}
