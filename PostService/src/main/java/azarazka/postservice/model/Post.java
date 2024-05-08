package azarazka.postservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document
public class Post {
    @Id
    private String id;
    private String authorId;
    private String message;
    private LocalDateTime createdAt;
    private List<String> likes = new ArrayList<>();

    public Post() {
    }

    public Post(String id, String authorId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post post)) {
            return false;
        }
        return Objects.equals(getId(), post.getId()) &&
                Objects.equals(getAuthorId(), post.getAuthorId()) &&
                Objects.equals(getCreatedAt(), post.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthorId(), getCreatedAt());
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", likes=" + likes +
                '}';
    }
}
