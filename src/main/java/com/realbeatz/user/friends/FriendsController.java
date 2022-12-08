package com.realbeatz.user.friends;

import com.realbeatz.exceptions.InvalidDeleteFriendException;
import com.realbeatz.exceptions.InvalidFriendRequestException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.payloads.requests.*;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.realbeatz.utils.HttpRequestUtils.getUsernameFromRequest;

/**
 * Controller solely for managing friend relationship between users
 */
@CrossOrigin(originPatterns = "*", origins = "*")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/user/friends")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getAllFriendsByUserId(
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Getting all friends of user with id: {}", username);

        List<UserDTO> friendsList;

        try {
            friendsList = friendsService.getAllFriends(username);
        } catch (InvalidUserIdException | InvalidUsernameException e) {
            log.error("Error getting friends of user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        log.info("Friends fetched by user {} : {}", username, friendsList);

        return ResponseEntity.ok(friendsList);
    }

    // todo: implement a controller for admins and super admins
    @Deprecated
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> addNewFriend(
            @RequestBody AddFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Adding new friend for " +
                "user with username: {}, request: {}", username, requestBody);

        try {
            friendsService.addNewFriend(username, requestBody.getNewFriendId());
        } catch (InvalidUserIdException | InvalidFriendRequestException | InvalidUsernameException e) {
            log.error("Error adding new friend for" +
                    " user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @Deprecated
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteFriend(
            @RequestBody DeleteFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Deleting friend for " +
                "user with username: {}, request: {}", username, requestBody);

        try {
            friendsService.deleteFriend(username, requestBody.getFriendId());
        } catch (InvalidUserIdException | InvalidDeleteFriendException | InvalidUsernameException e) {
            log.error("Error deleting friend for user with username: {}", username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createNewFriendRequest(
            @RequestBody AddFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Creating new friend request for user with username: {}, request{}",
                username, requestBody);

        try {
            friendsService.createNewFriendRequest(
                    username,
                    requestBody.getNewFriendId(),
                    requestBody.getMessage());
        } catch (InvalidUserIdException | InvalidFriendRequestException | InvalidUsernameException e) {
            log.error("Error creating new friend request for " +
                            "user with username: {}, request{}",
                    username, requestBody, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }


        return ResponseEntity.
                status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/request/confirm")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> confirmFriendRequest(
            @RequestBody ConfirmAddFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Confirming friend request from " +
                        "user with id: {} to user with username: {}",
                requestBody.getRequesterId(), username);

        try {
            friendsService.confirmFriendRequest(username, requestBody.getRequesterId());
        } catch (InvalidUserIdException | InvalidFriendRequestException | InvalidUsernameException e) {
            log.error("Error confirming friend request from" +
                            " user with id: {} to user with id: {}",
                    requestBody.getRequesterId(), username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/request/refuse")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> refuseFriendRequest(
            @RequestBody RefuseAddFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Refusing friend request from " +
                        "user with id: {} to user with username: {}",
                requestBody.getRequesterId(), username);

        try {
            friendsService.refuseFriendRequest(username, requestBody.getRequesterId());
        } catch (InvalidUserIdException | InvalidFriendRequestException | InvalidUsernameException e) {
            log.error("Error refusing friend request from " +
                            "user with id: {} to user with username: {}",
                    requestBody.getRequesterId(), username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/request/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteFriendRequestSent(
            @RequestBody DeleteAddFriendRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Deleting friend request from " +
                        "user with username: {} to user with id: {}",
                username, requestBody.getNewFriendId());

        try {
            friendsService.deleteFriendRequestSent(username, requestBody.getNewFriendId());
        } catch (InvalidUserIdException | InvalidFriendRequestException | InvalidUsernameException e) {
            log.error("Error deleting friend request from " +
                            "user with username: {} to user with id: {}",
                    requestBody.getNewFriendId(), username, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/request/all")
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getAllFriendRequestsReceived(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        log.info("Fetching all friend requests received for user: {}", username);

        List<FriendRequestDTO> friendRequestDTOs;

        try {
            friendRequestDTOs = friendsService.getAllFriendRequestsReceived(username);
        } catch (InvalidUsernameException e) {
            log.error("Error fetching all friend requests received for user: {}", username);
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.ok(friendRequestDTOs);
    }
}
