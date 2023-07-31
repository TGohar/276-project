package trackour.trackour.model.task;

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
public class TaskService {
    
    @Autowired
    private TaskRepository repository;

    public void createNewTask(Task task) {
        // printTaskObj(task);
        repository.saveAndFlush(task);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }
      
    public Optional<Task> findTaskById(String id) {
        return repository.findById(id);
    }
      
    // public List<Project> getAllByOwner(Long uid) {
    //     List<Project> userProjects = new ArrayList<>();
    //     for (Project project : this.getAllProjects()) {
    //         if (project.getOwner().equals(uid)){
    //             userProjects.add(project);
    //         }
    //     }
    //     return userProjects;
    // }
      
    public List<Task> findAllTasksByStatus(TaskStatus status) {
        List<Task> allMatchStatus = new ArrayList<>();
        for (Task task : this.getAllTasks()){
            if (task.getStatus().equals(status)){
                allMatchStatus.add(task);
            }
        }
        return allMatchStatus;
    }
      
    public void deleteTask(Task task) {
        repository.delete(task);
    }
      
    public void updateTask(Task task) {
        createNewTask(task);
    }

    /**
     * Prettyy print {@link `} object
     * @param user
     */
    public void printTaskObj(Task task) {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(SerializationFeature.INDENT_OUTPUT); //pretty print
        String objStr;
        try {
            objStr = objMapper.writeValueAsString(task);
            System.out.println(objStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
