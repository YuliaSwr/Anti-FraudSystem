package antifraud.controller;

import antifraud.entity.AppUser;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private UserService userService;

    @PutMapping("/api/auth/role")
    public AppUser changeRole(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String role = request.get("role");
        return userService.changeRole(username, role);
    }
}
