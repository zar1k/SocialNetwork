package azarazka.postservice.service;

import azarazka.postservice.client.UserClient;
import azarazka.postservice.model.Post;
import azarazka.postservice.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserClient userClient;

    @Autowired
    public PostService(PostRepository postRepository, UserClient userClient) {
        this.postRepository = postRepository;
        this.userClient = userClient;
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getPostById(String id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Post updatePost(String id, Post updatedPost) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost != null) {
            updatedPost.setId(id);
            return postRepository.save(updatedPost);
        }
        LOGGER.info("Post not found. ID:{}", id);
        return null;
    }

    public void likePost(String postId, String userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<String> likes = post.getLikes();
            if (!likes.contains(userId)) {
                likes.add(userId);
                post.setLikes(likes);
                postRepository.save(post);
            }
        }
    }

    public void removeLike(String postId, String userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            LOGGER.info("Remove like from post:{}, userId:{}", postId, userId);
            Post post = optionalPost.get();
            List<String> likes = post.getLikes();
            likes.remove(userId);
            post.setLikes(likes);
            postRepository.save(post);
        }
    }

    public Page<Post> getFeedForUser(String userId, Pageable pageable) {
        ResponseEntity<List<String>> response = this.userClient.getFollowing(userId);
        List<String> following = response.getBody();
        return postRepository.findAllByAuthorIdInOrderByCreatedAtDesc(following, pageable);
    }
}

