package ngo_management_backend.controller;

import ngo_management_backend.model.User;
import ngo_management_backend.model.dto.LoginDTO;
import ngo_management_backend.repository.UserRepository;

import java.util.Map;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO )
    {
        User user=userRepository.findByEmail(loginDTO.getEmail());
       if (user == null) {
        return ResponseEntity.status(404).body(Map.of("message", "User Not Found"));
    }

    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
        return ResponseEntity.status(401).body(Map.of("message", "Invalid Password"));
    }

    return ResponseEntity.ok(user);
    }
}
