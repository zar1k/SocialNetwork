package azarazka.postservice.repository;

import azarazka.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByAuthorIdInOrderByCreatedAtDesc(List<String> authorIds, Pageable pageable);
}
