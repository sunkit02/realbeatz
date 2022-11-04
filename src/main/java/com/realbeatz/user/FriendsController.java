package com.realbeatz.user;

import com.realbeatz.dto.Message;
import com.realbeatz.exception.InvalidDeleteFriendException;
import com.realbeatz.exception.InvalidFriendRequestException;
import com.realbeatz.exception.InvalidUserIdException;
import com.realbeatz.requests.FriendRequest;
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

    private final UserFriendsService friendsService;
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
                    .body(Message.of(e.getMessage()));
        }

        return ResponseEntity.ok(friendsList);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addNewFriend(
            @PathVariable("userId") Long userId,
            @RequestBody FriendRequest request) {

        log.info("Adding new friend for user with id: {}, request: {}", userId, request);

        try {
            friendsService.addNewFriend(userId, request.getFriendId());
        } catch (InvalidUserIdException | InvalidFriendRequestException e) {
            log.error("Error adding new friend for user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }
//        friendsService.addNewFriend2(userId, request.getFriendId());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<?> deleteFriend(
            @PathVariable("userId") Long userId,
            @RequestBody FriendRequest request) {

        log.info("Deleting friend for user with id: {}, request: {}", userId, request);

        try {
            friendsService.deleteFriend(userId, request.getFriendId());
        } catch (InvalidUserIdException | InvalidDeleteFriendException e) {
            log.error("Error deleting friend for user with id: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }
}
