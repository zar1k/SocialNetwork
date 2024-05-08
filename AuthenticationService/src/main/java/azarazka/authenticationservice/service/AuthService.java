package azarazka.authenticationservice.service;

import azarazka.authenticationservice.client.UserClient;
import azarazka.authenticationservice.dto.AuthRequest;
import azarazka.authenticationservice.dto.AuthResponse;
import azarazka.authenticationservice.dto.LoginUserDto;
import azarazka.authenticationservice.dto.RegisterUser;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final String salt;

    public AuthService(JwtUtil jwtUtil, UserClient userClient, @Value("${salt}") String salt) {
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
        this.salt = salt;
    }

    public RegisterUser register(AuthRequest request) {
        request.setPassword(BCrypt.hashpw(request.getPassword(), salt));
        ResponseEntity<RegisterUser> response = this.userClient.registerUser(request);
        LOGGER.info("Registered user:{}", response.getBody());
        return response.getBody();
    }

    public AuthResponse authenticate(LoginUserDto loginUserDto) {
        loginUserDto.setPassword(BCrypt.hashpw(loginUserDto.getPassword(), salt));
        LOGGER.info("Find authenticate user by email:{}", loginUserDto.getEmail());
        ResponseEntity<RegisterUser> response = this.userClient.authenticate(loginUserDto);
        RegisterUser registerUser = response.getBody();

        if (registerUser == null) {
            LOGGER.error("Unable to authenticate user by email:{}", loginUserDto.getEmail());
            return null;
        }

        String accessToken = jwtUtil.generate(registerUser.id(), "ACCESS");
        String refreshToken = jwtUtil.generate(registerUser.id(), "REFRESH");

        return new AuthResponse(accessToken, refreshToken);
    }
}