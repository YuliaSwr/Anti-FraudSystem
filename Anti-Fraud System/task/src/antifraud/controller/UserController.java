package antifraud.controller;

import antifraud.entity.AppUser;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser register(@RequestBody AppUser user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userService.register(user);
        return user;
    }

    @PutMapping("/role")
    public AppUser changeRole(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String role = request.get("role");
        return userService.changeRole(username, role);
    }

    @PutMapping("/access")
    public Map<String, String> lock(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String operation = request.get("operation");
        return userService.setAccess(username, operation);
    }

    @GetMapping("/list")
    public List<AppUser> getAllUser() {
        return userService.getAll();
    }

    @DeleteMapping("/user/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        return userService.delete(username);
    }
}

