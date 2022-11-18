package com.realbeatz.user.friends;

import com.realbeatz.dto.ErrorMessage;
import com.realbeatz.exceptions.InvalidDeleteFriendException;
import com.realbeatz.exceptions.InvalidFriendRequestException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.requests.*;
import com.realbeatz.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller solely for managing friend relationship between users
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("user/friends")
public class FriendsController {

    private final FriendsService friendsService;
//    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllFriendsByUserId(
            @PathVariable("userId") Long userId) {
        log.info("Getting all friends of user with id: {}", userId);

        List<UserDTO> friendsList;

        try {
            friendsList = friendsService.getAllFriendsByUserId(userId);
        } catch (InvalidUserIdException e) {
            log.error("Error getting friends of user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(friendsList);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addNewFriend(
            @PathVariable("userId") Long userId,
            @RequestBody AddFriendRequest request) {

        log.info("Adding new friend for " +
                "user with id: {}, request: {}", userId, request);

        try {
            friendsService.addNewFriend(userId, request.getNewFriendId());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error adding new friend for" +
                    " user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
//        friendsService.addNewFriend2(userId, request.getFriendId());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<?> deleteFriend(
            @PathVariable("userId") Long userId,
            @RequestBody DeleteFriendRequest request) {

        log.info("Deleting friend for " +
                "user with id: {}, request: {}", userId, request);

        try {
            friendsService.deleteFriend(userId, request.getFriendId());
        } catch (InvalidUserIdException | InvalidDeleteFriendException e) {
            log.error("Error deleting friend for user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/request")
    public ResponseEntity<?> createNewFriendRequest(
            @PathVariable("userId") Long userId,
            @RequestBody AddFriendRequest request) {
        log.info("Creating new friend request for user with id: {}, request{}",
                userId, request);

        try {
            friendsService.createNewFriendRequest(
                    userId,
                    request.getNewFriendId(),
                    request.getMessage());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error creating new friend request for " +
                            "user with id: {}, request{}",
                    userId, request, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }


        return ResponseEntity.
                status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/{userId}/request/confirm")
    public ResponseEntity<?> confirmFriendRequest(
            @PathVariable("userId") Long userId,
            @RequestBody ConfirmAddFriendRequest request) {
        log.info("Confirming friend request from " +
                        "user with id: {} to user with id: {}",
                request.getRequesterId(), userId);

        try {
            friendsService.confirmFriendRequest(userId, request.getRequesterId());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error confirming friend request from" +
                            " user with id: {} to user with id: {}",
                    request.getRequesterId(), userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/request/refuse")
    public ResponseEntity<?> refuseFriendRequest(
            @PathVariable("userId") Long userId,
            @RequestBody RefuseAddFriendRequest request) {
        log.info("Refusing friend request from " +
                        "user with id: {} to user with id: {}",
                request.getRequesterId(), userId);

        try {
            friendsService.refuseFriendRequest(userId, request.getRequesterId());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error refusing friend request from " +
                            "user with id: {} to user with id: {}",
                    request.getRequesterId(), userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/request/delete")
    public ResponseEntity<?> deleteFriendRequestSent(
            @PathVariable("userId") Long userId,
            @RequestBody DeleteAddFriendRequest request) {
        log.info("Deleting friend request from " +
                        "user with id: {} to user with id: {}",
                userId, request.getNewFriendId());

        try {
            friendsService.deleteFriendRequestSent(userId, request.getNewFriendId());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error deleting friend request from " +
                            "user with id: {} to user with id: {}",
                    request.getNewFriendId(), userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }
}
