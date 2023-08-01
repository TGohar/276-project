package trackour.trackour.model.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.atmosphere.config.service.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;
import trackour.trackour.model.user.User;
import trackour.trackour.model.user.UserRepository;

@Service
@Transactional
public class ProjectsService {
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Project> getAllByOwner(User user) {
        if (user != null){
            return projectRepository.findByOwner(user);
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
      
    public Optional<Project> findProjectById(String id) {
        return projectRepository.findById(id);
    }
      
      
    // public List<Project> getAllByOwner(User user) {
    //     if (user == null){
    //         return null;
    //     }
    //     List<Project> userProjects = new ArrayList<>();
    //     for (Project project : this.getAllProjects()) {
    //         if (project.getOwner().equals(user)){
    //             userProjects.add(project);
    //         }
    //     }
    //     return userProjects;
    // }
      
    public List<Project> findAllCompletedProjects() {
        List<Project> allCompleted = new ArrayList<>();
        for (Project project : this.getAllProjects()){
            if (project.getStatus().equals(ProjectStatus.COMPLETED)){
                allCompleted.add(project);
            }
        }
        return allCompleted;
    }
      
    public List<Project> findAllInCompleteTask() {
        List<Project> allInProgress = new ArrayList<>();
        for (Project project : this.getAllProjects()){
            if (project.getStatus().equals(ProjectStatus.IN_PROGRESS)){
                allInProgress.add(project);
            }
        }
        return allInProgress;
    }
      
    public void deleteTask(Project task) {
        projectRepository.delete(task);
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
