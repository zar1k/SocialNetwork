package azarazka.userservice.controller;

import azarazka.userservice.dto.AuthRequest;
import azarazka.userservice.dto.LoginUserDto;
import azarazka.userservice.dto.RegisterUser;
import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterUser> registerUser(@RequestBody AuthRequest authRequest) {
        RegisterUser user = this.userService.registerUser(authRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RegisterUser> authenticate(@RequestBody LoginUserDto loginUserDto) {
        RegisterUser authenticate = this.userService.authenticate(loginUserDto);
        return ResponseEntity.ok(authenticate);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable String userId) {
        List<UserResponse> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<String>> getFollowing(@PathVariable String userId) {
        List<String> followers = userService.getFollowing(userId);
        return ResponseEntity.ok(followers);
    }

    @PostMapping("/{userId}/follow/{targetUserId}")
    public ResponseEntity<?> followUser(@PathVariable String userId, @PathVariable String targetUserId) {
        userService.followUser(userId, targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
