package ngo_management_backend.controller;

import ngo_management_backend.model.NGOStatus;
import ngo_management_backend.model.Role;
import ngo_management_backend.model.User;
import ngo_management_backend.model.dto.LoginDTO;
import ngo_management_backend.repository.UserRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Path UPLOAD_DIR = Paths.get("uploads/licenses");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == Role.NGO) {
            user.setNgoStatus(NGOStatus.PENDING);
        }
        User saved = userRepository.save(user);
        Map<String, Object> body = new HashMap<>();
        body.put("id", saved.getId());
        body.put("email", saved.getEmail());
        body.put("role", saved.getRole() != null ? saved.getRole().name() : null);
        body.put("name", saved.getName());
        body.put("ngoStatus", saved.getNgoStatus() != null ? saved.getNgoStatus().name() : "N/A");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
        }
        if (user.getRole() == Role.NGO && user.getNgoStatus() != NGOStatus.APPROVED) {
            String msg = user.getNgoStatus() == NGOStatus.REJECTED
                ? "Your NGO registration was rejected. Please contact support."
                : "Your NGO registration is pending admin approval.";
            return ResponseEntity.status(403).body(Map.of("message", msg));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("id", user.getId());
        body.put("email", user.getEmail());
        body.put("role", user.getRole() != null ? user.getRole().name() : null);
        body.put("name", user.getName());
        body.put("ngoStatus", user.getNgoStatus() != null ? user.getNgoStatus().name() : "N/A");
        body.put("latitude", user.getLatitude());
        body.put("longitude", user.getLongitude());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/uploadLicense")
    public ResponseEntity<?> uploadLicense(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No file provided"));
        }
        try {
            Files.createDirectories(UPLOAD_DIR);
            String originalName = file.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
            String filename = UUID.randomUUID().toString() + ext;
            Files.copy(file.getInputStream(), UPLOAD_DIR.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("filename", filename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Failed to save file: " + e.getMessage()));
        }
    }

    @GetMapping("/license/{filename}")
    public ResponseEntity<Resource> getLicense(@PathVariable String filename) {
        try {
            Path filePath = UPLOAD_DIR.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ngos")
    public ResponseEntity<List<User>> getAllNgos() {
        return ResponseEntity.ok(userRepository.findByRole(Role.NGO));
    }

    @GetMapping("/ngos/pending")
    public ResponseEntity<List<User>> getPendingNgos() {
        return ResponseEntity.ok(userRepository.findByRoleAndNgoStatus(Role.NGO, NGOStatus.PENDING));
    }

    @PutMapping("/ngos/{id}/approve")
    public ResponseEntity<?> approveNgo(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User ngo = opt.get();
        ngo.setNgoStatus(NGOStatus.APPROVED);
        userRepository.save(ngo);
        return ResponseEntity.ok(Map.of("message", "NGO approved"));
    }

    @PutMapping("/ngos/{id}/reject")
    public ResponseEntity<?> rejectNgo(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User ngo = opt.get();
        ngo.setNgoStatus(NGOStatus.REJECTED);
        userRepository.save(ngo);
        return ResponseEntity.ok(Map.of("message", "NGO rejected"));
    }
}
