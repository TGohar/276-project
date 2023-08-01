package trackour.trackour.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProjectsService {
    
    @Autowired
    private ProjectRepository repository;

    public void createNewProject(Project project) {
        printProjectObj(project);
        repository.saveAndFlush(project);
    }

    public List<Project> getAllProjects() {
        return repository.findAll();
    }
      
    public Optional<Project> findProjectById(String id) {
        return repository.findById(id);
    }
      
    public List<Project> getAllByOwner(Long uid) {
        List<Project> userProjects = new ArrayList<>();
        for (Project project : this.getAllProjects()) {
            if (project.getOwner().equals(uid)){
                userProjects.add(project);
            }
        }
        return userProjects;
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
        repository.delete(task);
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
}
