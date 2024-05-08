package azarazka.commentservice.repository;

import azarazka.commentservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findAllByPostId(String postId, Pageable pageable);
}
