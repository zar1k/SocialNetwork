package azarazka.userservice.controller;

import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getFollowers_ReturnsListOfFollowers() {
        String userId = "user123";
        List<UserResponse> followers = new ArrayList<>();
        followers.add(new UserResponse("Jack Read", "j.read@gmail.com"));
        followers.add(new UserResponse("Jack Black", "j.black@gmail.com"));

        when(userService.getFollowers(userId)).thenReturn(followers);
        ResponseEntity<List<UserResponse>> response = userController.getFollowers(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(followers, response.getBody());
        verify(userService, times(1)).getFollowers(userId);
    }

    @Test
    void followUser_SuccessfullyFollowsUser() {
        String userId = "user123";
        String targetUserId = "targetUser123";

        ResponseEntity<?> response = userController.followUser(userId, targetUserId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).followUser(userId, targetUserId);
    }
}
