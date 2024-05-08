package azarazka.postservice.controller;

import azarazka.postservice.model.Post;
import azarazka.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;

    @Test
    void getAllPosts_ReturnsPosts() {
        Post post1 = new Post("1", "author-id-1", "Content 1", LocalDateTime.now());
        Post post2 = new Post("2", "author-id-2", "Content 2", LocalDateTime.now());
        when(postService.getAllPosts(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(post1, post2)));

        ResponseEntity<Page<Post>> responseEntity = postController.getAllPosts(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).getTotalElements());
        assertEquals(post1, responseEntity.getBody().getContent().get(0));
        assertEquals(post2, responseEntity.getBody().getContent().get(1));
    }

    @Test
    void getPostById_ExistingPost_ReturnsPost() {
        Post post = new Post("1", "author-id-1", "Message", LocalDateTime.now());
        when(postService.getPostById("1")).thenReturn(post);

        ResponseEntity<Post> responseEntity = postController.getPostById("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(post, responseEntity.getBody());
    }

    @Test
    void getPostById_NonExistingPost_ReturnsNotFound() {
        when(postService.getPostById("3")).thenReturn(null);

        ResponseEntity<Post> responseEntity = postController.getPostById("3");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void createPost_ReturnsCreatedPost() {
        Post post = new Post("1", "author-id-1", "New Message", LocalDateTime.now());
        when(postService.createPost(post)).thenReturn(post);

        ResponseEntity<Post> responseEntity = postController.createPost(post);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(post, responseEntity.getBody());
    }

    @Test
    void updatePost_ExistingPost_ReturnsUpdatedPost() {
        Post post = new Post("1", "author-id-1", "Updated Message", LocalDateTime.now());
        when(postService.updatePost("1", post)).thenReturn(post);

        ResponseEntity<Post> responseEntity = postController.updatePost("1", post);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(post, responseEntity.getBody());
    }

    @Test
    void likePost_ReturnsCreated() {
        ResponseEntity<?> responseEntity = postController.likePost("1", "user-id-1");

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void removeLike_ReturnsOk() {
        ResponseEntity<?> responseEntity = postController.removeLike("1", "user-id-1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}

