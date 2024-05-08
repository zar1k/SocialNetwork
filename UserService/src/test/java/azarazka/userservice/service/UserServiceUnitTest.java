package azarazka.userservice.service;

import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.model.User;
import azarazka.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceUnitTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_NAME_1 = "user1";
    private static final String USER_NAME_2 = "user2";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFollowers_ReturnsListOfFollowers_WhenUserExists() {
        User user1 = new User(USER_ID_1, USER_NAME_1, Collections.singletonList(USER_ID_2), List.of());
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user1));
        User user2 = new User(USER_ID_2, USER_NAME_2, List.of(), List.of());
        when(userRepository.findAllById(anyList())).thenReturn(List.of(user2));

        List<UserResponse> followers = userService.getFollowers(USER_ID_1);

        assertEquals(1, followers.size());
        assertEquals(user2.getUsername(), followers.getFirst().username());
        verify(userRepository, times(1)).findById(USER_ID_1);
        verify(userRepository, times(1)).findAllById(anyList());
    }

    @Test
    void getFollowers_ReturnsEmptyList_WhenUserNotFound() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.empty());

        List<UserResponse> followers = userService.getFollowers(USER_ID_1);

        assertTrue(followers.isEmpty());
        verify(userRepository, times(1)).findById(USER_ID_1);
    }

    @Test
    void followUser_SuccessfullyFollowsUser() {
        User user = new User(USER_ID_1, USER_NAME_1, new ArrayList<>(), new ArrayList<>());
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user));

        User userToFollow = new User(USER_ID_2, USER_NAME_2, new ArrayList<>(), new ArrayList<>());
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.of(userToFollow));

        userService.followUser(USER_ID_1, USER_ID_2);

        List<String> following = user.getFollowing();
        List<String> followers = userToFollow.getFollowers();
        assertTrue(following.contains(USER_ID_2));
        assertTrue(followers.contains(USER_ID_1));

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(userToFollow);
    }

    @Test
    void followUser_DoesNotFollowUser_WhenUserNotFound() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.empty());

        userService.followUser(USER_ID_1, USER_ID_2);

        verify(userRepository, never()).save(any());
    }
}
