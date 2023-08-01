package trackour.trackour.model.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.task.TaskRepository;
import trackour.trackour.model.user.User;
import trackour.trackour.model.user.UserRepository;

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
    
    
    public void createNewProject(Project project) {
        // printProjectObj(project);
        projectRepository.saveAndFlush(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
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
}
