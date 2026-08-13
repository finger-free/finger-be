package finger.example.demo.auth.controller;

import finger.example.demo.auth.domain.dto.request.LoginRequest;
import finger.example.demo.auth.domain.dto.response.LoginResponse;
import finger.example.demo.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/{provider}")
    public LoginResponse login(@PathVariable String provider, @RequestBody LoginRequest request) {
        return authService.login(provider, request);
    }
}
