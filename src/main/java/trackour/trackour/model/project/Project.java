package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
// import java.util.Set;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.user.User;

@Entity
@Table( name="projects", uniqueConstraints= @UniqueConstraint(columnNames={"project_id"}))
public class Project {
    
    @Id
    @Column(name = "project_id", length = 36, nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    // a project can have one owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User owner;

    @Column(name = "title")
    private String title;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    
    // projects can be categorized by status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;
    
    // is this project collaborative
    @Column(name = "collaboration_mode")
    private CollaborationMode collaborationMode;
    

    // a project can have many participants
    private Set<Long> participants;

    // progress double. can be used by progress bar or charts
    @Column(name = "progress")
    private Double progress;

    @Column(name = "keys")
    private Set<String> keys;

    @Column(name = "bpm")
    private Set<String> bpm;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks;

    
    // ----------methods------------------------------------------
    public Project() {
        // for hiberbnate
    }
    public Project(User owner) {
        initCollections();
        this.owner = owner;
    }
    public void initCollections() {
        initStatus();

        this.progress = 0.0;
        this.title = "New Project";
        this.collaborationMode = CollaborationMode.SOLO;
        // Initialize the collections as empty sets
        this.participants = new HashSet<>();
        this.keys = new HashSet<>();
        this.bpm = new HashSet<>();
    }

    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }    

    private void initStatus() {
        this.status = ProjectStatus.IN_PROGRESS;
    }

    public Set<String> getKeys() {
        return keys;
    }
    
    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }
    
    public Set<String> getBpm() {
        return bpm;
    }
    
    public void setBpm(Set<String> bpm) {
        this.bpm = bpm;
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

    public Set<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Long> participants) {
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
