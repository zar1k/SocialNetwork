package azarazka.userservice.controller;

import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerAPITest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getFollowers_ReturnsListOfFollowers() throws Exception {
        String userId = "user123";
        List<UserResponse> followers = new ArrayList<>();
        followers.add(new UserResponse("Jack Read", "j.read@gmail.com"));
        followers.add(new UserResponse("Jack Black", "j.black@gmail.com"));

        when(userService.getFollowers(userId)).thenReturn(followers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}/followers", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(followers.size()));
    }

    @Test
    void followUser_SuccessfullyFollowsUser() throws Exception {
        String userId = "user123";
        String targetUserId = "targetUser123";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/{userId}/follow/{targetUserId}", userId, targetUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
