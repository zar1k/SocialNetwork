package azarazka.userservice;

import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.model.User;
import azarazka.userservice.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceApplicationIntegrationTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_ID_3 = "user-id-3";
    private static final String USER_NAME_1 = "user1";
    private static final String USER_NAME_2 = "user2";
    private static final String USER_NAME_3 = "user3";

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;

    static {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getFollowers_ReturnsListOfFollowers() {
        User user1 = new User(USER_ID_1, USER_NAME_1, Arrays.asList(USER_ID_2, USER_ID_3), Arrays.asList(USER_ID_2, USER_ID_3));
        User user2 = new User(USER_ID_2, USER_NAME_2, Arrays.asList(USER_ID_1, USER_ID_3), List.of());
        User user3 = new User(USER_ID_3, USER_NAME_3, List.of(USER_ID_1), List.of());
        userRepository.saveAll(Arrays.asList(user1, user2, user3));

        UserResponse[] response = RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/api/users/{userId}/followers", USER_ID_1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserResponse[].class);

        assertEquals(2, response.length);
        assertEquals(user2.getUsername(), response[0].username());
        assertEquals(user3.getUsername(), response[1].username());
    }

    @Test
    void followUser_SuccessfullyFollowsUser() {
        User user1 = new User(USER_ID_1, USER_NAME_1, List.of(USER_ID_2), List.of());
        User user2 = new User(USER_ID_2, USER_NAME_2, List.of(), List.of());
        userRepository.saveAll(Arrays.asList(user1, user2));

        RestAssured.given()
                .contentType("application/json")
                .when()
                .post("/api/users/{userId}/follow/{targetUserId}", USER_ID_2, USER_ID_1)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        User updatedUser = userRepository.findById(USER_ID_2).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getFollowing().size());
        assertEquals(USER_ID_1, updatedUser.getFollowing().getFirst());
    }
}
