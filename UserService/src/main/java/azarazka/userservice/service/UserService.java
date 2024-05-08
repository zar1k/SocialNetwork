package azarazka.userservice.service;

import azarazka.userservice.dto.AuthRequest;
import azarazka.userservice.dto.LoginUserDto;
import azarazka.userservice.dto.RegisterUser;
import azarazka.userservice.dto.UserResponse;
import azarazka.userservice.model.User;
import azarazka.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getFollowers(String userId) {
        LOGGER.info("Find user followers by ID:{}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<String> followers = user.getFollowers();
            List<User> allById = this.userRepository.findAllById(followers);
            return allById.stream().map(u -> new UserResponse(u.getUsername(), u.getEmail()))
                    .toList();
        } else {
            LOGGER.info("User not found by ID:{}", userId);
            return Collections.EMPTY_LIST;
        }
    }

    public List<String> getFollowing(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getFollowing();
        } else {
            LOGGER.info("User not found. ID:{}", userId);
            return Collections.EMPTY_LIST;
        }
    }

    public void followUser(String userId, String userToFollowId) {
        LOGGER.info("Find current user by ID:{}", userId);
        User user = userRepository.findById(userId).orElse(null);
        LOGGER.info("Find user to follow, ID:{}", userToFollowId);
        User userToFollow = userRepository.findById(userToFollowId).orElse(null);

        if (user != null && userToFollow != null) {
            List<String> following = user.getFollowing();
            List<String> followers = userToFollow.getFollowers();

            if (!following.contains(userToFollowId)) {
                following.add(userToFollowId);
                followers.add(userId);

                user.setFollowing(following);
                userToFollow.setFollowers(followers);

                userRepository.save(user);
                userRepository.save(userToFollow);
            }
        }
    }

    public RegisterUser registerUser(AuthRequest authRequest) {
        User user = new User(
                authRequest.getName(),
                authRequest.getEmail(),
                authRequest.getPassword());
        User save = this.userRepository.save(user);
        LOGGER.info("Save user to db, name:{}, email:{}", authRequest.getName(), authRequest.getEmail());
        return new RegisterUser(
                save.getId(),
                save.getUsername(),
                save.getEmail());
    }

    public RegisterUser authenticate(LoginUserDto loginUserDto) {
        LOGGER.info("Find user by email:{}", loginUserDto.email());
        Optional<User> optionalUser =
                this.userRepository.findUserByEmailAndPassword(loginUserDto.email(), loginUserDto.password());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new RegisterUser(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail());
        }
        LOGGER.info("User not found by email:{}", loginUserDto.email());
        return null;
    }
}