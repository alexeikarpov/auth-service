package ru.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.authservice.dto.*;
import ru.authservice.entity.CustomUserDetails;
import ru.authservice.entity.User;
import ru.authservice.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    private ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest) {
        if (userService.findByLogin(userRequest.getLogin()).isPresent())
            return ResponseEntity.badRequest().body("Login is already taken");
        if (userService.findByEmail(userRequest.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email is already in use");
        if (userRequest.getPassword().length() < 6)
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        userService.registerUser(userRequest);
        return ResponseEntity.ok().body("Registration successful");
    }

    @PostMapping("/login")
    private ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );
            // Получаем объект CustomUserDetails
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            // Получаем объект User из CustomUserDetails
            User user = customUserDetails.getUser();

            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("jwt", jwt);
            tokens.put("refreshToken", refreshToken);

            return ResponseEntity.ok(tokens);  // Возвращаем токены клиенту
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @GetMapping("/getall")
    private List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
