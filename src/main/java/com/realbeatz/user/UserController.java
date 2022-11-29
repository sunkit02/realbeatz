package com.realbeatz.user;

import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.post.PostDTO;
import com.realbeatz.post.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.realbeatz.utils.HttpRequestUtils.getUsernameFromRequest;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    // todo: move to admin controller
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "isDto",
                    required = false,
                    defaultValue = "false") Boolean isDto) {
        if (isDto)
            return ResponseEntity.ok(userService.getAllUserDTOs());
        else
            return ResponseEntity.ok(userService.getAllUsers());
    }

    // todo: remove option to get actual user object
    @GetMapping
    @PreAuthorize("hasAnyAuthority('user:read','admin:read')")
    public ResponseEntity<?> getUserByUsername(
            @RequestParam(name = "isDto",
                    required = false,
                    defaultValue = "true") Boolean isDto,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        // return dto version of user if indicated
        if (isDto)
            try {

                return ResponseEntity.ok(userService.getUserDTObyUsername(username));

            } catch (InvalidUsernameException e) {
                log.error("Error getting user with username: {}", username, e);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorMessage.of(e.getMessage()));
            }

        // return actual user object
        try {

            return ResponseEntity.ok(userService.getUserByUsername(username));

        } catch (InvalidUsernameException e) {
            log.error("Error getting user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
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
}

