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
import trackour.trackour.model.project.Project;
import trackour.trackour.model.project.ProjectRepository;
import trackour.trackour.model.user.User;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    public void createNewTask(Task task) {
        // printTaskObj(task);
        taskRepository.saveAndFlush(task);
    }

    public List<Task> getAllByProject(Long id) {
        if (id != null){
            Optional<Project> currentUserOptional = projectRepository.findById(id);
            if (currentUserOptional.isPresent()) {
                // keeping the database access open
                return projectRepository.findById(id).get().getTasks();
            }
        }
        return new ArrayList<>();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
      
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // public Project getProject() {
    //     return taskRepository.findB
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
        if (task != null) {
            taskRepository.delete(task);
        }
    }
      
    public void updateTask(Task task) {
        createNewTask(task);
    }

    public void updateTask(Task task, boolean doRecalculateProgress) {
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
