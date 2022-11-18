package com.realbeatz.user;

import com.realbeatz.dto.ErrorMessage;
import com.realbeatz.exceptions.DuplicateUsernameException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.post.PostDTO;
import com.realbeatz.post.PostService;
import com.realbeatz.requests.RegisterUserRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "isDto",
                    required = false,
                    defaultValue = "false") Boolean isDto) {
        if (isDto)
            return ResponseEntity.ok(userService.getAllUserDTOs());
        else
            return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "isDto",
                    required = false,
                    defaultValue = "false") Boolean isDto) {
        // return dto version of user if indicated
        if (isDto)
            try {
                return ResponseEntity.ok(userService.getUserDTOById(userId));
            } catch (InvalidUserIdException e) {
                log.error("Error getting user with id: {}", userId, e);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorMessage.of(e.getMessage()));
            }

        // return actual user object
        User user;
        try {
            user = userService.getUserById(userId);
        } catch (InvalidUserIdException e) {
            log.error("Error getting user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterUserRequest request) {

        log.info("Request: {}", request);

        UserDTO userDTO;

        try {
            userDTO = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getLastName(),
                    request.getFirstName(),
                    request.getDob(),
                    request.getBio());
        } catch (DuplicateUsernameException | InvalidUserInputException e) {
            log.error("Error processing request: {}", request, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    // todo: add patch method used to update user properties
    @PatchMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> updates) {
        log.info("Updating user with id: {}, updates: {}", userId, updates);

        UserDTO userDTO;
        try {
            userDTO = userService.updateUser(userId, updates);
        } catch (Exception e) {
            log.error("Error updating user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/update/{userId}/profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> updates) {

        log.info("Updating user profile with id: {}, updates: {}", userId, updates);

        UserDTO userDTO;
        try {
            userDTO = userService.updateUserProfile(userId, updates);
        } catch (Exception e) {
            log.error("Error updating user profile with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable("userId") Long userId) {

        log.info("Deleting user with id: {}", userId);

        try {
            userService.deleteUser(userId);
        } catch (InvalidUserIdException e) {
            log.error("Error deleting user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<?> getAllPostsByUser(
            @PathVariable("userId") Long userId) {

        log.info("Getting all posts by user with id: {}", userId);

        List<PostDTO> posts;

        try {
            posts = postService.getAllPostsByUser(userId);
        } catch (InvalidUserIdException e) {
            log.error("Error getting all posts by user with id: {}", userId, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(posts);
    }
}

