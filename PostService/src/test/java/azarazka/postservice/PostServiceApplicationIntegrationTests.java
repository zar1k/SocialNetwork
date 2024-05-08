package azarazka.postservice;

import azarazka.postservice.model.Post;
import azarazka.postservice.repository.PostRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostServiceApplicationIntegrationTests {
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    @LocalServerPort
    private int port;
    @Autowired
    private PostRepository postRepository;


    static {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
    }

    @Test
    void testCreateAndGetPost() {
        Post post = new Post("1", "author-id-1", "Test message", LocalDateTime.now());

        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .when()
                .get("/api/posts/{id}", "1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo("1"))
                .body("authorId", equalTo("author-id-1"))
                .body("message", equalTo("Test message"));
    }

    @Test
    void testGetAllPosts() {
        Post post1 = new Post("1", "author-id-1", "Message 1", LocalDateTime.now());
        Post post2 = new Post("2", "author-id-2", "Message 2", LocalDateTime.now());
        List<Post> posts = List.of(post1, post2);
        postRepository.saveAll(posts);

        Response response = given()
                .when()
                .get("/api/posts")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<Post> content = response.getBody().path("content");

        assertEquals(posts.size(), content.size());
    }

    @Test
    void testUpdatePost() {
        Post post = new Post("1", "user1", "Original message", LocalDateTime.now());
        postRepository.save(post);

        Post updatedPost = new Post("1", "user1", "Updated message", LocalDateTime.now());

        given()
                .contentType(ContentType.JSON)
                .body(updatedPost)
                .when()
                .put("/api/posts/{id}", "1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("message", equalTo("Updated message"));
    }

    @Test
    void testLikePost() {
        Post post = new Post("1", "user1", "Test message", LocalDateTime.now());
        postRepository.save(post);

        given()
                .when()
                .post("/api/posts/{postId}/like/{userId}", "1", "user2")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .when()
                .get("/api/posts/{id}", "1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("likes.size()", equalTo(1))
                .body("likes", contains("user2"));
    }

    @Test
    void testRemoveLike() {
        Post post = new Post("1", "user1", "Test message", LocalDateTime.now());
        post.setLikes(Arrays.asList("user2", "user3"));
        postRepository.save(post);

        given()
                .when()
                .delete("/api/posts/{postId}/like/{userId}", "1", "user2")
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .when()
                .get("/api/posts/{id}", "1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("likes.size()", equalTo(1))
                .body("likes", contains("user3"));
    }
}
