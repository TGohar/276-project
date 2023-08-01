package trackour.trackour.model.user;
import java.time.LocalDateTime;
import java.util.ArrayList;
// import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// import java.util.stream.Collectors;
import java.util.UUID;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.transaction.Transactional;
import trackour.trackour.model.project.Project;

@Entity
@Table(name="Users", uniqueConstraints=@UniqueConstraint(columnNames={"uid", "username", "email"}))
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "friends_with",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friendsWith;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pending_friend_requests",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "requester_id")
    )
    private List<User> pendingFriendRequests;
    
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Project> ownedProjects;

    @Column(name = "username")
    private String username;

    @Column(name = "passwordResetToken")
    private String passwordResetToken;
    
    @DateTimeFormat
    @Column(name = "passwordResetTokenCreatedAt")
    private LocalDateTime passwordResetTokenCreatedAt;
    
    private String displayName;
    
    private String password;
    
    @Column(name = "email")
    private String email;
    
    @Type(ListArrayType.class)
    @Column(name = "friend_requests", columnDefinition = "bigint[]")
    private List<Long> friendRequests;
    
    @Type(ListArrayType.class)
    @Column(name = "friends", columnDefinition = "bigint[]")
    private List<Long> friends;

    
    // roles are now stored in a set directly in the roles column of the users table
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;    

    // --------------methods------------------------------

    public User() {
        // for hibernate
        initCollections();
    }

    public User(String username, String displayName, String password, String email, Set<Role> roles) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.email = email;
        this.initCollections();
    }

    public User(String username, String displayName, String password, String email) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.email = email;
        ownedProjects = new ArrayList<>();
        this.initCollections();
    }
    
    private void initCollections() {
        this.friendRequests = new ArrayList<>();
        this.friends = new ArrayList<>();
        
        this.friendsWith = new ArrayList<>();
        this.pendingFriendRequests = new ArrayList<>();

        this.ownedProjects = new ArrayList<>();
        
        // initialize default role as ["USER"]
        Set<Role> defaultRole = new HashSet<>();
        // defaultRole.add(Role.USER);
        defaultRole.add(Role.ADMIN);
        setRoles(defaultRole);
    }

    public List<User> getFriendsWith() {
        return friendsWith;
    }

    public void setFriendsWith(List<User> friendsWith) {
        this.friendsWith = friendsWith;
    }

    public List<User> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(List<User> pendingFriendRequests) {
        this.pendingFriendRequests = pendingFriendRequests;
    }

    @Transactional
    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    @Transactional
    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }
    

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getUid() {
        return this.uid;
    }
    

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
    

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
    

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
    

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public void generatePasswordResetToken() {
        this.passwordResetToken = UUID.randomUUID().toString();
    }

    public String getPasswordResetToken() {
        return this.passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenCreatedAt() {
        return this.passwordResetTokenCreatedAt;
    }

    public void setPasswordResetTokenCreatedAt(LocalDateTime passwordResetTokenCreatedAt) { 
        this.passwordResetTokenCreatedAt = passwordResetTokenCreatedAt;
    }    

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public List<Long> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<Long> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }
}
