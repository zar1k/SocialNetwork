package azarazka.postservice.controller;

import azarazka.postservice.model.Post;
import azarazka.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerAPITest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;

    @Test
    void getAllPosts_ReturnsAllPosts() throws Exception {
        Post post1 = new Post("1", "Title 1", "Content 1", LocalDateTime.now());
        Post post2 = new Post("2", "Title 2", "Content 2", LocalDateTime.now());
        when(postService.getAllPosts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(post1, post2)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].message").value("Content 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].message").value("Content 2"));
    }

    @Test
    void getPostById_ExistingPost_ReturnsPost() throws Exception {
        Post post1 = new Post("1", "Title 1", "Content 1", LocalDateTime.now());
        when(postService.getPostById("1")).thenReturn(post1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Content 1"));
    }

    @Test
    void getPostById_NonExistingPost_ReturnsNotFound() throws Exception {
        when(postService.getPostById("3")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createPost_ReturnsCreatedPost() throws Exception {
        when(postService.createPost(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Post.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":\"111\",\"message\":\"New Content\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("New Content"));
    }

    @Test
    void updatePost_NonExistingPost_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":\"111\",\"message\":\"Updated Content\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void likePost_ReturnsCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}/like/{userId}", "1", "1"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void removeLike_ReturnsOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}/like/{userId}", "1", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
