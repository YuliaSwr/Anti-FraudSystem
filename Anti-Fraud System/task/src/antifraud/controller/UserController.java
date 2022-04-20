package antifraud.controller;

import antifraud.entity.AppUser;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    private Object Map;

    @PostMapping("/api/auth/user")
    public ResponseEntity register(@RequestBody AppUser user) {
        if (user.getUsername()==null || user.getPassword()==null || user.getName()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userService.register(user);
        return new ResponseEntity(user, HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public List<AppUser> getAllUser() {
        return userService.getAll();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        return userService.delete(username);
    }
}

