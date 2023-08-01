package trackour.trackour.model.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vaadin.flow.component.Component;

import jakarta.transaction.Transactional;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.task.TaskRepository;
import trackour.trackour.model.user.User;
import trackour.trackour.model.user.UserRepository;
import trackour.trackour.views.components.camelotwheel.Key;

@Service
@Transactional
public class ProjectsService {
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<Project> getAllByOwner(User user) {
        if (user != null){
            Optional<User> currentUserOptional = userRepository.findByUid(user.getUid());
            if (currentUserOptional.isPresent()) {
                // keeping the database access open
                return userRepository.findByUid(user.getUid()).get().getOwnedProjects();
            }
        }
        return new ArrayList<>();
    }

    public List<Task> getAllTasksForProject(Long projectId) {
        if (projectRepository.findById(projectId).isPresent()){ 
            return projectRepository.findById(projectId).get().getTasks();
        }
        return Arrays.asList();
    }

    public int getBpm(Project project) {
        if (projectRepository.findById(project.getId()).isPresent()) {
            return projectRepository.findById(project.getId()).get().getBpm();
        }
        return 0;
    }

    public void setBpm(int newBpm, Project project) {
        project.setBpm(newBpm);
        updateProject(project);
    }

    public void setParticipants(Set<String> users, Project project) {
        Set<Long> participants = new HashSet<>();
        for (String usrname : users) {
            if (userRepository.findByUsername(usrname).isPresent()){
                participants.add(userRepository.findByUsername(usrname).get().getUid());
            }
        }
        project.setParticipants(participants);
        updateProject(project);
    }

    public void setKeys(Set<Key> keys, Project project) {
        Set<Key> keysSet = new HashSet<>();
        for (Key key : keys) {
            keysSet.add(key);
        }
        project.setKeys(keysSet);
        updateProject(project);
    }

    public Set<Long> getAllParticipantIdsForProject(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            return projectRepository.findById(projectId).get().getParticipants();
        }
        return new HashSet<>();
    }
    
    // /**
    //  * pass the project id and the list of friends usernames
    //  * @param projectId
    //  * @param usernames
    //  */
    // public void setParticipants(Long projectId, List<String> usernames) {
    //     if (projectRepository.findById(projectId).isPresent()) {
    //         boolean allPresent = usernames.stream()
    //         .allMatch(username -> userRepository.findByUsername(username).isPresent());
    //         if (allPresent) {
    //             // the project and user exist
    //             Set<User> selectedParticipants = usernames.stream()
    //                 .filter(username -> userRepository.findByUsername(username).isPresent()) // filter only the usernames that are present in the userRepository
    //                 .map(username -> userRepository.findByUsername(username).get()) // map each username to the corresponding User object
    //                 .collect(Collectors.toSet()); // collect the User objects into a Set
    //             Project chosenProject = projectRepository.findById(projectId).get();
    //             chosenProject.setParticipants(selectedParticipants);
    //             updateProject(chosenProject);

    //         }
    //     }
    // }

    // public void unsetParticipants(Long projectId, List<Long> userIds) {
    //     if (projectRepository.findById(projectId).isPresent()) {
    //         // make sure the ids are for valid users
    //         boolean allPresent = userIds.stream()
    //             .allMatch(userId -> userRepository.findByUid(userId).isPresent());
    //         if (allPresent) {
    //             // the project and user exist
    //             Set<User> selectedParticipants = userIds.stream()
    //                 .filter(id1 -> userRepository.findByUid(id1).isPresent()) // filter only the id that are present in the userRepository
    //                 .map(id2 -> userRepository.findByUid(id2).get()) // map each username to the corresponding User object
    //                 .collect(Collectors.toSet()); // collect the User objects into a Set
    //             Project chosenProject = projectRepository.findById(projectId).get();
    //             chosenProject.getParticipants().removeIf(user -> selectedParticipants.stream()
    //                 .map(User::getUsername) // map each user in selectedParticipants to their username
    //                 .anyMatch(username -> username.equals(user))); // remove all the selected participants from the project
    //             updateProject(chosenProject);
    //         }
    //     }
    // }
    
    
    public void createNewProject(Project project) {
        // printProjectObj(project);
        projectRepository.saveAndFlush(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public boolean projectExists(Long id) {
        return projectRepository.findById(id).isPresent() ? true : false; 
    }
      
    public Optional<Project> findProjectById(Long id) {
        return projectRepository.findById(id);
    }
      
    public List<Project> findAllCompletedProjects() {
        List<Project> allCompleted = new ArrayList<>();
        for (Project project : this.getAllProjects()){
            if (project.getStatus().equals(ProjectStatus.COMPLETED)){
                allCompleted.add(project);
            }
        }
        return allCompleted;
    }
      
    public List<Project> findAllInProgressTask() {
        List<Project> allInProgress = new ArrayList<>();
        for (Project project : this.getAllProjects()){
            if (project.getStatus().equals(ProjectStatus.IN_PROGRESS)){
                allInProgress.add(project);
            }
        }
        return allInProgress;
    }

    public List<Project> findAllCompletedProjectsByOwner(User user) {
        List<Project> allCompleted = new ArrayList<>();
        for (Project project : getAllByOwner(user)){
            if (project.getStatus().equals(ProjectStatus.COMPLETED)){
                allCompleted.add(project);
            }
        }
        return allCompleted;
    }
      
    public List<Project> findAllInProgressTaskByOwner(User user) {
        List<Project> allInProgress = new ArrayList<>();
        for (Project project : getAllByOwner(user)){
            if (project.getStatus().equals(ProjectStatus.IN_PROGRESS)){
                allInProgress.add(project);
            }
        }
        return allInProgress;
    }
      
    public void deleteTask(Task task) {
        if (task == null) {
            return;
        }
        taskRepository.deleteById(task.getId());
    }
      
    public void updateProject(Project project) {
        createNewProject(project);
    }

    /**
     * Prettyy print {@link User} object
     * @param user
     */
    public void printProjectObj(Project project) {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(SerializationFeature.INDENT_OUTPUT); //pretty print
        String objStr;
        try {
            objStr = objMapper.writeValueAsString(project);
            System.out.println(objStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void deleteProject(Project project) {
        if (project != null) {
            projectRepository.delete(project);
        }
    }

    public Project getById(Long id) {
        if (projectRepository.findById(id).isPresent()) {
            return projectRepository.findById(id).get();
        }
        return null;
    }
}
