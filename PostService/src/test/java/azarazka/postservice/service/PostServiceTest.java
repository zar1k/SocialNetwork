package azarazka.postservice.service;

import azarazka.postservice.model.Post;
import azarazka.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPosts_ReturnsAllPosts() {
        // Mock data
        List<Post> posts = List.of(
                new Post("1", "user1", "Message 1", LocalDateTime.now()),
                new Post("2", "user2", "Message 2", LocalDateTime.now())
        );
        Page<Post> page = new PageImpl<>(posts);
        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Call service method
        Page<Post> result = postService.getAllPosts(PageRequest.of(0, 10));

        // Verify result
        assertEquals(posts.size(), result.getTotalElements());
        assertTrue(result.getContent().containsAll(posts));
    }

    @Test
    void getPostById_ExistingPost_ReturnsPost() {
        // Mock data
        Post post = new Post("1", "user1", "Message 1", LocalDateTime.now());
        when(postRepository.findById("1")).thenReturn(Optional.of(post));

        // Call service method
        Post result = postService.getPostById("1");

        // Verify result
        assertEquals(post, result);
    }

    @Test
    void getPostById_NonExistingPost_ReturnsNull() {
        // Mock data
        when(postRepository.findById("3")).thenReturn(Optional.empty());

        // Call service method
        Post result = postService.getPostById("3");

        // Verify result
        assertNull(result);
    }

    @Test
    void createPost_ReturnsCreatedPost() {
        // Mock data
        Post post = new Post("1", "user1", "New Message", LocalDateTime.now());
        when(postRepository.save(any())).thenReturn(post);

        // Call service method
        Post result = postService.createPost(post);

        // Verify result
        assertEquals(post, result);
    }

    @Test
    void updatePost_ExistingPost_ReturnsUpdatedPost() {
        // Mock data
        Post existingPost = new Post("1", "user1", "Existing Message", LocalDateTime.now());
        Post updatedPost = new Post("1", "user1", "Updated Message", LocalDateTime.now());
        when(postRepository.findById("1")).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any())).thenReturn(updatedPost);

        // Call service method
        Post result = postService.updatePost("1", updatedPost);

        // Verify result
        assertEquals(updatedPost, result);
    }

    @Test
    void updatePost_NonExistingPost_ReturnsNull() {
        // Mock data
        when(postRepository.findById("3")).thenReturn(Optional.empty());

        // Call service method
        Post result = postService.updatePost("3", new Post());

        // Verify result
        assertNull(result);
    }

    @Test
    void likePost_AddsLike() {
        // Mock data
        Post post = new Post("1", "user1", "Message", LocalDateTime.now());
        when(postRepository.findById("1")).thenReturn(Optional.of(post));

        // Call service method
        postService.likePost("1", "user2");

        // Verify like is added
        assertTrue(post.getLikes().contains("user2"));
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void likePost_UserAlreadyLiked_PostNotUpdated() {
        // Mock data
        Post post = new Post("1", "user1", "Message", LocalDateTime.now());
        post.getLikes().add("user2");
        when(postRepository.findById("1")).thenReturn(Optional.of(post));

        // Call service method
        postService.likePost("1", "user2");

        // Verify post is not updated
        verify(postRepository, never()).save(any());
    }

    @Test
    void removeLike_RemovesLike() {
        // Mock data
        Post post = new Post("1", "user1", "Message", LocalDateTime.now());
        post.getLikes().add("user2");
        when(postRepository.findById("1")).thenReturn(Optional.of(post));

        // Call service method
        postService.removeLike("1", "user2");

        // Verify like is removed
        assertFalse(post.getLikes().contains("user2"));
        verify(postRepository, times(1)).save(post);
    }
}
