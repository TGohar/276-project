package trackour.trackour.model;

import java.util.Collection;
import java.util.List;
// import java.util.HashSet;
import java.util.Optional;
// import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;
        public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> getByUid(Long uid) {
        return repository.findByUid(uid);
    }

    public Optional<User> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> getByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> getByPasswordResetToken(String passwordResetToken) {
        return repository.findByPasswordResetToken(passwordResetToken);
    }

    public User update(User entity) {
        return repository.saveAndFlush(entity);
    }

    public void delete(Long uid) {
        repository.deleteByUid(uid);
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public int count() {
        return (int) repository.count();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getAuthorities(user.get()));
        }
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        System.out.println(user.getDisplayName() + " has roles:");
        return user.getRoles().stream().map(authority -> {
            System.out.println(authority.roleToString());
            return new SimpleGrantedAuthority(authority.roleToRoleString());
        }).collect(Collectors.toList());
    }
    
    /**
     * Prettyy print {@link User} object
     * @param user
     */
    public void printUserObj(User user) {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(SerializationFeature.INDENT_OUTPUT); //pretty print
        String objStr;
        try {
            objStr = objMapper.writeValueAsString(user);
            System.out.println(objStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * eg. Call {@code passwordEncoder().encode("rawPasscode");} to encrypt "rawPasscode".
     * To just get an instance of the encoder, call {@code passwordEncoder()}
     * @return {@link BCryptPasswordEncoder} encrypted String
     */
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Recieve new user credentials, validates then updates database with this new user,
     * @param username
     * @param password
     */
    public boolean registerUser(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        // unable to properly implement hashing technique atm so will either drop that or tryb again later
        String encodedPassword = passwordEncoder().encode(password);
        newUser.setPassword(encodedPassword);
        printUserObj(newUser);
        return this.submitUser(newUser);
    }

    public boolean registerUser(User newUser) {
        // unable to properly implement hashing technique atm so will either drop that or tryb again later
        String encodedPassword = passwordEncoder().encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        printUserObj(newUser);

        // extra validation
        // if displayName was submitted as empty, use the username string in place of it
        if (newUser.getDisplayName().isEmpty()){
            newUser.setDisplayName(newUser.getUsername());
        }
        return this.submitUser(newUser);
    }

    private boolean submitUser(User newUser) {
        // if user doesn't already exist do new registration
        Optional<User> existingUser = getByUsername(newUser.getUsername());
        if (!existingUser.isPresent()){
            update(newUser);
            return true;
        }   
        return false;
    }

    public boolean updatePassword(User oldUser) {
        Optional<User> existingUser = getByUsername(oldUser.getUsername());
        if(!existingUser.isPresent()){
            return false;
        }

        String encodedPassword = passwordEncoder().encode(oldUser.getPassword());
        oldUser.setPassword(encodedPassword);
        printUserObj(oldUser);

        // invalidate the used token with an empty string
        oldUser.setPasswordResetToken("");

        update(oldUser);

        return true;
    }

    //only use this one if no changes to the password have been made
    public boolean updateUser(User user) {
        Optional<User> existingUser = getByUsername(user.getUsername());
        if(!existingUser.isPresent()){
            return false;
        }

        update(user);
        
        return true;
    }
}
