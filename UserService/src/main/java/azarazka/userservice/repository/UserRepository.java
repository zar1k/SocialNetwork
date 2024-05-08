package azarazka.userservice.repository;

import azarazka.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmailAndPassword(String email, String password);
}
