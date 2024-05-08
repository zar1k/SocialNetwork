package azarazka.authenticationservice.controller;

import azarazka.authenticationservice.dto.AuthRequest;
import azarazka.authenticationservice.dto.AuthResponse;
import azarazka.authenticationservice.dto.LoginUserDto;
import azarazka.authenticationservice.dto.RegisterUser;
import azarazka.authenticationservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<RegisterUser> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        AuthResponse response = this.authService.authenticate(loginUserDto);
        return ResponseEntity.ok(response);
    }
}
