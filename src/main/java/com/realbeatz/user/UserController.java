package com.realbeatz.user;

import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.post.PostDTO;
import com.realbeatz.post.PostService;
import com.realbeatz.utils.FileUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.realbeatz.utils.HttpRequestUtils.getUsernameFromRequest;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PostService postService;


    // todo: move to admin controller
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('user:read','admin:read')")
    public ResponseEntity<?> getAllUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUserDTOs());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('user:read','admin:read')")
    public ResponseEntity<?> getUserByJwtCredentials(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        log.info("Fetching user with username: {}", username);

        // return dto version of user if indicated
        try {

            return ResponseEntity.ok(userService.getUserDTObyUsername(username));

        } catch (InvalidUsernameException e) {
            log.error("Error getting user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
    }

    /**
     * Returns a UserDTO based on username or user id entered
     *
     * @param userInfo   username or user id
     * @param isUsername flag to determine whether username or user id is being requested
     * @return UserDTO requested
     */
    @GetMapping("/{userInfo}")
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getUserByIdOrUsername(
            @PathVariable String userInfo,
            @RequestParam(
                    value = "isUsername",
                    defaultValue = "false",
                    required = false) Boolean isUsername) {

        // Get UserDTO by username
        if (isUsername) {
            log.info("Fetching user with username: {}", userInfo);
            try {
                UserDTO userDTO = userService.getUserDTObyUsername(userInfo);
                return ResponseEntity.ok(userDTO);
            } catch (InvalidUsernameException e) {
                log.error("Error fetching user with username: {}", userInfo, e);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorMessage.of(e.getMessage()));
            }
        } else {
            // Get UserDTO by userId
            Long userId = Long.valueOf(userInfo);
            log.info("Fetching user with id: {}", userId);
            try {
                UserDTO userDTO = userService.getUserDTOById(userId);
                return ResponseEntity.ok(userDTO);
            } catch (InvalidUserIdException e) {
                log.error("Error fetching user with id: {}", userId, e);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorMessage.of(e.getMessage()));
            }
        }
    }

    // todo: add patch method used to update user properties
    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(
            @RequestBody Map<String, String> updates,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Updating user with id: {}, updates: {}", username, updates);

        UserDTO userDTO;
        try {
            userDTO = userService.updateUser(username, updates);
        } catch (Exception e) {
            log.error("Error updating user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/update/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUserProfile(
            @RequestBody Map<String, String> updates,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Updating user profile with username: {}, updates: {}", username, updates);

        UserDTO userDTO;
        try {
            userDTO = userService.updateUserProfile(username, updates);
        } catch (Exception e) {
            log.error("Error updating user profile with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('user:write', 'admin:write')")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Deleting user with username: {}", username);

        try {
            userService.deleteUser(username);
        } catch (InvalidUserIdException e) {
            log.error("Error deleting user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getAllPostsByUser(
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Getting all posts by user with id: {}", username);

        List<PostDTO> posts;

        try {
            posts = postService.getAllPostsByUser(username);
        } catch (InvalidUsernameException e) {
            log.error("Error getting all posts by user with username: {}", username, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping(value = "/profile-pictures/{fileFullName}", produces = "multipart/form-data")
    public ResponseEntity<?> getProfilePic(
            @PathVariable String fileFullName) throws IOException {
        log.info("Fetching profile picture with file code: {}", fileFullName);
        byte[] bytes = FileUtils.getProfilePictureAsBinary(fileFullName);
        return ResponseEntity.ok(bytes);
    }
}

