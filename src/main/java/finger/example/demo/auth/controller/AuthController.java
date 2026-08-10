package finger.example.demo.auth.controller;

import finger.example.demo.auth.domain.dto.request.GoogleLoginRequest;
import finger.example.demo.auth.domain.dto.response.GoogleLoginResponse;
import finger.example.demo.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public GoogleLoginResponse loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        return authService.loginWithGoogle(request);
    }
}
