package trackour.trackour.model.project;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
// import java.util.Set;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table( name="projects", uniqueConstraints= @UniqueConstraint(columnNames={"project_id"}))
public class Project {
    
    @Id
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
    
    private Set<String> tasks;
    
    // is this project collaborative
    @Column(name = "collaboration_mode")
    private CollaborationMode collaborationMode;
    
    // a project can have one owner
    @Column(name = "owner", nullable = false)
    private Long owner;

    // a project can have many participants
    private Set<Long> participants;

    // progress double. can be used by progress bar or charts
    @Column(name = "progress")
    private Double progress;

    @Column(name = "keys")
    private Set<String> keys;

    @Column(name = "bpm")
    private Set<String> bpm;

    
    // ----------methods------------------------------------------
    private Project() {
        initCollections();
        this.owner = -1l;
    }
    public Project(Long owner) {
        initCollections();
        this.owner = owner;
    }
    public void initCollections() {
        initStatus();

        this.progress = 0.0;
        this.title = "New Project";
        this.collaborationMode = CollaborationMode.SOLO;
        // Initialize the collections as empty sets
        this.tasks = new HashSet<>();
        this.participants = new HashSet<>();
        this.keys = new HashSet<>();
        this.bpm = new HashSet<>();
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

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public Set<String> getTasks() {
        return tasks;
    }
    
    public void setTasks(Set<String> tasks) {
        this.tasks = tasks;
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
