package backend.controller;

import backend.dto.LoginRequest;
import backend.model.User;
import backend.repository.UserRepository;
import backend.security.JwtService;
import backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Invalid email or password"));
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "token", token
                )
        );
    }
}