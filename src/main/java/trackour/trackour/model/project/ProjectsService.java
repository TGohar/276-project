package trackour.trackour.model.project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;
import trackour.trackour.model.task.Task;
import trackour.trackour.model.task.TaskRepository;
import trackour.trackour.model.task.TaskStatus;
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

    public User getProjectOwner(Long projectId) {
        Optional<Project> projectOptional = findProjectById(projectId);
        if (projectOptional.isPresent()) {
            return projectOptional.get().getOwner();
        }
        return null;
    }

    public List<Project> getAllByParticipation(User user) {
        List<Project> participatedProjects = new ArrayList<>();
        List<Project> all = projectRepository.findAll();
        for (Project project : all){
            if (getAllParticipantIdsForProject(project.getId()).contains(user.getUid())){
                if (project.getCollaborationMode().equals(CollaborationMode.TEAM)){
                    participatedProjects.add(project);
                }
            }
        }
        return participatedProjects;
    }
    
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

    public void setParticipants(Set<User> users, Project project) {
        Set<Long> participants = new HashSet<>();
        for (User usr : users) {
            participants.add(usr.getUid());
        }
        project.setParticipants(participants);
        updateProject(project);
    }

    public void setParticipantsByLong(Set<Long> users, Project project) {
        Set<Long> participants = new HashSet<>();
        for (Long usr : users) {
            participants.add(usr);
        }
        project.setParticipants(participants);
        updateProject(project);
    }

    public List<Key> getKeys(Long id){
        List<Key> keys = new ArrayList<>();
        if (getById(id) == null){
            return keys;
        }
        for (Key k : getById(id).getKeys()) {
            keys.add(k);
        }
        return keys;
    }

    public void setKeys(Set<Key> keys, Project project) {
        Set<Key> keysSet = new HashSet<>();
        for (Key key : keys) {
            keysSet.add(key);
        }
        project.setKeys(keysSet);
        updateProject(project);
    }

    public Set<User> getAllParticipantsForProject(Long projectId) {
        Set<Long> getAllIds = getAllParticipantIdsForProject(projectId);
        Set<User> allParticipants = new HashSet<>();
        for (Long id : getAllIds) {
            Optional<User> userOptional = userRepository.findByUid(projectId);
            if (userOptional.isPresent()){
                allParticipants.add(userOptional.get());
            }
        }
        return allParticipants;
    }

    public Set<Long> getAllParticipantIdsForProject(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            return projectRepository.findById(projectId).get().getParticipants();
        }
        return new HashSet<>();
    }

    public List<String> getAllParticipantIdsForProjectAsString(Long projectId) {
        List<String> lst = new ArrayList<>();
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            for (Long id : projectRepository.findById(projectId).get().getParticipants()) {
                lst.add(id.toString());
            }
        }
        return lst;
    }

    public  boolean isValidParticipant(Long userId, Long projectId) {
        boolean retVal = false;
        for (Project project : getAllProjects()) {
            if (project.getId() == projectId) {
                if (projectRepository.findById(projectId).get().getOwner().getUid() == userId){
                    retVal = true;
                }
                // if (project.getParticipants() == null){
                //     return false;
                // }
                // else if (project.getParticipants().isEmpty()) {
                //     return false;
                // }
                if (project.getParticipants().contains(userId)){
                    if (project.getCollaborationMode().equals(CollaborationMode.TEAM)){
                        retVal = true;
                    }
                }
            }
        }
        return retVal;
    }
    
    
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

    public Integer getBpm(Long id) {
        Integer bpm = 80;
        Project proj = getById(id);
        if (proj == null){
            return bpm;
        }
        List<Integer> bpmValue = proj.getBpm().stream().map(Integer::parseInt).collect(Collectors.toList());
        if (bpmValue.get(0) == null){
            return bpm;
        }
        bpm = bpmValue.get(0);
        return bpm;
    }

    public Project getById(Long id) {
        if (projectRepository.findById(id).isPresent()) {
            return projectRepository.findById(id).get();
        }
        return null;
    }

    public List<Task> getAllTasksByProject(Long projectId) {
        if (projectId != null){
            Optional<Project> projOptional = projectRepository.findById(projectId);
            if (projOptional.isPresent()) {
                // keeping the database access open
                return projectRepository.findById(projectId).get().getTasks();
            }
        }
        return new ArrayList<>();
    }

    public void updateProgress(Long projectId) {
        Optional<Project> projectOptional = findProjectById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            List<Task> tasks = getAllTasksByProject(projectId);
            int completeCount = (int) tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED) // keep only the tasks with status COMPLETE
            .count();
            Double newProgress = (double)completeCount / tasks.size();
            if (newProgress == 1){
                project.setStatus(ProjectStatus.COMPLETED);
            }
            else {
                project.setStatus(ProjectStatus.IN_PROGRESS);
            }
            project.setProgress(newProgress);
            System.out.println("updateProgress!: " + newProgress);
            updateProject(project);
        }
    }

    public double getProgress(Long id) {
        double progress = projectRepository.findById(id).get().getProgress();
        if (progress > 1) {
            return 1;
        }
        else return projectRepository.findById(id).get().getProgress();
    }

    public String getOwner(Long projectId) {
        Project proj = getById(projectId);
        if (proj != null) {
            return proj.getOwner().getUsername();
        }
        return "UNKNOWN";
    }
}
