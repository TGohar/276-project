package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
// import java.util.Set;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.user.User;
import trackour.trackour.views.components.camelotwheel.Key;

@Entity
@Table( name="project", uniqueConstraints= @UniqueConstraint(columnNames={"project_id"}))
public class Project {
    
    @Id
    @Column(name = "project_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // a project can have one owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "uid")
    private User owner;

    // set of user ids allowed on this project
    private Set<Long> participants;

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

    // progress double. can be used by progress bar or charts
    @Column(name = "progress")
    private Double progress;

    @Column(name = "keys")
    private Set<Key> keys;

    @Column(name = "bpm")
    private Integer bpm;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
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
        this.bpm = 80;
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

    public Set<Key> getKeys() {
        return keys;
    }
    
    public void setKeys(Set<Key> keys) {
        this.keys = keys;
    }
    
    public Integer getBpm() {
        return bpm;
    }
    
    public void setBpm(Integer bpm) {
        this.bpm = bpm;
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
