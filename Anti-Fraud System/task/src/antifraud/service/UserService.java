package antifraud.service;

import antifraud.entity.AppUser;
import antifraud.entity.UserRole;
import antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User" + username + " is NOT found!"));
    }

    public void register(AppUser user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (userRepository.count() == 0) {
            user.setRole(UserRole.ADMINISTRATOR);
            user.setLocked(false);
        } else {
            user.setRole(UserRole.MERCHANT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Map<String, String> delete(String username) {

        AppUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        userRepository.deleteById(user.getId());
        return Map.of("username", username,
                "status", "Deleted successfully!");
    }

    public List<AppUser> getAll() {
        return userRepository.findAll();
    }

    public AppUser changeRole(String username, String role) {
        AppUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (role.equals(user.getRole().name())) {
           throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (role.equals(UserRole.SUPPORT.name())) {
            user.setRole(UserRole.SUPPORT);
        }

        if (role.equals(UserRole.ADMINISTRATOR.name())) {
            user.setRole(UserRole.ADMINISTRATOR);
        }

        return user;
    }
}
