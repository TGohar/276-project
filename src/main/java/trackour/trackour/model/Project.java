package trackour.trackour.model;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name="Projects", 
    uniqueConstraints=
        @UniqueConstraint(
            columnNames={"id", "owner"}
            )
        )
public class Project {
    
    @Id
    @JsonIgnore
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;


    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;


    @Column(name = "owner")
    private Long owner;

    @Type(ListArrayType.class)
    @Column(name = "participants", columnDefinition = "bigint[]")
    private List<Long> participants;

    // projects can be categorized by status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
