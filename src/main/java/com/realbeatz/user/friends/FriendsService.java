package com.realbeatz.user.friends;

import com.realbeatz.exceptions.InvalidDeleteFriendException;
import com.realbeatz.exceptions.InvalidFriendRequestException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.user.User;
import com.realbeatz.user.UserDTO;
import com.realbeatz.user.UserRepository;
import com.realbeatz.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.realbeatz.user.friends.FriendRequestStatus.*;

@Service
@AllArgsConstructor
public class FriendsService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public List<UserDTO> getAllFriends(Long userId) throws InvalidUserIdException {
        User user = userService.getUserById(userId);
        return getAllFriends(user);
    }

    public List<UserDTO> getAllFriends(String username) throws InvalidUserIdException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        return getAllFriends(user);
    }

    public List<UserDTO> getAllFriends(User user) {
        return user.getFriends().stream()
                .map(UserDTO::map)
                .toList();
    }

    public void addNewFriend(Long userId, Long friendId) throws InvalidUserIdException, InvalidFriendRequestException {
        User user = userService.getUserById(userId);
        addNewFriend(user, friendId);
    }

    public void addNewFriend(String username, Long friendId) throws InvalidUserIdException, InvalidFriendRequestException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        addNewFriend(user, friendId);
    }


    public void addNewFriend(User user, Long friendId) throws InvalidUserIdException, InvalidFriendRequestException {
        // check if user is adding oneself as friend
        if (user.getId().equals(friendId)) {
            throw new InvalidUserIdException(
                    "User cannot add oneself as friend");
        }

        User friend = userService.getUserById(friendId);

        Set<User> userFriends = user.getFriends();
        Set<User> friendFriends = friend.getFriends();

        // check if friendship has already been established
        if (userFriends.contains(friend)) {
            throw new InvalidFriendRequestException(
                    "User with id: " + user.getId() +
                            " already is friends with id : " + friendId);
        }
        if (friendFriends.contains(user)) {
            throw new InvalidFriendRequestException(
                    "User with id: " + friendId +
                            " already is friends with id : " + user.getId());
        }

        // establish friendship
        userFriends.add(friend);
        friendFriends.add(user);

        userService.save(user);
        userService.save(friend);
    }

    // adding friend relationship using native sql (works)
    @Transactional
    public void addNewFriend2(Long userId, Long friendId) {
        userRepository.addFriends(userId, friendId);
        userRepository.addFriends(friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) throws InvalidUserIdException, InvalidDeleteFriendException {
        if (userId.equals(friendId)) {
            throw new InvalidUserIdException(
                    "User cannot delete oneself as friend");
        }

        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);

        deleteFriend(user, friend);
    }

    public void deleteFriend(String username, Long friendId) throws InvalidUserIdException, InvalidDeleteFriendException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        User friend = userService.getUserById(friendId);
        if (user.getId().equals(friendId)) {
            throw new InvalidUserIdException(
                    "User cannot delete oneself as friend");
        }

        deleteFriend(user, friend);
    }


    public void deleteFriend(User user, User friend) throws InvalidUserIdException, InvalidDeleteFriendException {

        Set<User> userFriends = user.getFriends();
        Set<User> friendFriends = friend.getFriends();

        // check if friendship exists
        if (!userFriends.contains(friend)) {
            throw new InvalidDeleteFriendException(
                    "User with username: " + user.getUsername() + " doesn't have a friend " +
                            "with username: " + friend.getUsername());
        }
        if (!friendFriends.contains(user)) {
            throw new InvalidDeleteFriendException(
                    "User with username: " + user.getUsername() + " doesn't have a friend " +
                            "with username: " + friend.getUsername());
        }


        userFriends.remove(friend);
        friendFriends.remove(user);

        userService.save(user);
        userService.save(friend);
    }

    public void createNewFriendRequest(Long userId, Long newFriendId, String message) throws InvalidUserIdException, InvalidFriendRequestException {
        User user = userService.getUserById(userId);
        User newFriend = userService.getUserById(newFriendId);

        createNewFriendRequest(user, newFriend, message);
    }

    public void createNewFriendRequest(String username, Long newFriendId, String message) throws InvalidUserIdException, InvalidFriendRequestException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        User newFriend = userService.getUserById(newFriendId);

        createNewFriendRequest(user, newFriend, message);
    }


    // todo: add input validation for message
    public void createNewFriendRequest(User user, User newFriend, String message) throws InvalidUserIdException, InvalidFriendRequestException {


        boolean friendAdded = user.getFriends().contains(newFriend);

        // check if the two ids are the same
        if (user.getId().equals(newFriend.getId())) {
            throw new InvalidFriendRequestException(
                    "User with username: " + user.getUsername() + " is not allowed " +
                            "to request to add oneself as friend");
        }


        // check if relationship has already been established
        if (friendAdded) {
            throw new InvalidFriendRequestException(
                    "User with username: " + user.getUsername() +
                            " already has user with username: " + newFriend.getUsername()
                            + " as a friend");
        }

        // check if any similar friend requests has already been created
        List<FriendRequest> requestsSent = user.getFriendRequestsSent().stream()
                .filter(request -> request.getNewFriend().equals(newFriend))
                .toList();

        // throws an error when all there is a previous friends request
        // that hasn't been processed yet
        if (!requestsSent.isEmpty()) {
            boolean hasUnprocessedRequest = requestsSent.stream()
                    .anyMatch(request -> request.getStatus().equals(SENT));
            if (hasUnprocessedRequest) {
                throw new InvalidFriendRequestException(
                        "Friend request for adding user with username: " + newFriend.getUsername() +
                                " as a new friend for user with username: " + user.getUsername() +
                                " already exists");
            }
        }

        // create friend request
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(user)
                .newFriend(newFriend)
                .message(message == null ? "" : message)
                .status(SENT)
                .build();

        friendRequestRepository.save(friendRequest);

        System.out.println("\n".repeat(10));
        System.out.println(UserDTO.map(userService.getUserById(user.getId())));
        System.out.println(UserDTO.map(userService.getUserById(newFriend.getId())));
    }

    public void confirmFriendRequest(Long userId, Long requesterId) throws InvalidUserIdException, InvalidFriendRequestException {
        User user = userService.getUserById(userId);
        User requester = userService.getUserById(requesterId);

        confirmFriendRequest(user, requester);
    }

    public void confirmFriendRequest(String username, Long requesterId) throws InvalidUserIdException, InvalidFriendRequestException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        User requester = userService.getUserById(requesterId);

        confirmFriendRequest(user, requester);
    }

    public void confirmFriendRequest(User user, User requester) throws InvalidUserIdException, InvalidFriendRequestException {

        // check if request exists or has already been processed
        getUnprocessedFriendRequestsReceived(user, requester.getId()); // throw exception if it doesn't

        // todo: find more efficient way to process requests
        // process request

        // check if current user has also sent out a friend request to the requester
        // set the status sent by current user to CONFIRMED if present
        user.getFriendRequestsSent().stream()
                .filter(request -> request.getNewFriend().getId().equals(requester.getId()))
                .findFirst()
                .ifPresent(request -> request.setStatus(CONFIRMED));
        // set the status of request received to CONFIRMED
        user.getFriendRequestsReceived().stream()
                .filter(request -> request.getNewFriend().getId().equals(user.getId()))
                .findFirst()
                .ifPresent(request -> request.setStatus(CONFIRMED));

        // establish friendship between two users
        user.getFriends().add(requester);
        requester.getFriends().add(user);

        userService.save(user);
        userService.save(requester);
    }

    public void refuseFriendRequest(Long userId, Long requesterId) throws InvalidUserIdException, InvalidFriendRequestException {
        User user = userService.getUserById(userId);
        User requester = userService.getUserById(requesterId);

        refuseFriendRequest(user, requester);
    }

    public void refuseFriendRequest(String username, Long requesterId) throws InvalidUserIdException, InvalidFriendRequestException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        User requester = userService.getUserById(requesterId);

        refuseFriendRequest(user, requester);
    }


    public void refuseFriendRequest(User user, User requester) throws InvalidUserIdException, InvalidFriendRequestException {

        // check if request exists or has already been processed
        List<FriendRequest> unprocessedFriendRequestsReceived =
                getUnprocessedFriendRequestsReceived(user, requester.getId());

        // process request
        // check if current user has also sent out a friend request to the requester
        // delete the request if present
        user.getFriendRequestsSent().stream()
                .filter(request -> request.getNewFriend().getId().equals(requester.getId()))
                .filter(request -> request.getStatus().equals(SENT))
                .findFirst()
                .ifPresent(request ->
                        requester.getFriendRequestsReceived().remove(request));

        // set the status of the request received to REFUSED
        unprocessedFriendRequestsReceived.get(0).setStatus(REFUSED);

        userService.save(user);
        userService.save(requester);
    }

    public void deleteFriendRequestSent(Long userId, Long newFriendId) throws InvalidUserIdException, InvalidFriendRequestException {
        User user = userService.getUserById(userId);

        deleteFriendRequestSent(user, newFriendId);
    }
    public void deleteFriendRequestSent(String username, Long newFriendId) throws InvalidUserIdException, InvalidFriendRequestException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);

        deleteFriendRequestSent(user, newFriendId);
    }


    public void deleteFriendRequestSent(User user, Long newFriendId) throws InvalidFriendRequestException {

        // find all friend requests that sent to new friend
        List<FriendRequest> unprocessedFriendRequestsSent = getUnprocessedFriendRequestsSent(user, newFriendId);

        // ensure that an unprocessed request exists
        if (unprocessedFriendRequestsSent.isEmpty()) {
            throw new InvalidFriendRequestException(String.format(
                    "Friend request between users with username: %s and " +
                            "user with id: %d doesn't exist",
                    user.getUsername(), newFriendId));
        }

        // delete friend request if exists
        FriendRequest friendRequest = unprocessedFriendRequestsSent.get(0);
        user.getFriendRequestsSent().remove(friendRequest);

        userService.save(user);
    }

    private List<FriendRequest> getUnprocessedFriendRequestsReceived(User user, Long requesterId) throws InvalidFriendRequestException {
        List<FriendRequest> friendRequests = user.getFriendRequestsReceived().stream()
                .filter(request -> request.getRequester().getId().equals(requesterId))
                .toList();
        if (friendRequests.isEmpty()) {
            throw new InvalidFriendRequestException(String.format(
                    "Friend request between users with ids: %d and %d doesn't exist",
                    user.getId(), requesterId));
        }

        List<FriendRequest> unprocessedFriendRequestsReceived = friendRequests.stream()
                .filter(request -> request.getStatus().equals(SENT))
                .toList();

        if (unprocessedFriendRequestsReceived.isEmpty()) {
            throw new InvalidFriendRequestException(String.format(
                    "Friend request between users with ids: %d and %d " +
                            "has already been processed",
                    user.getId(), requesterId));
        }
        return unprocessedFriendRequestsReceived;
    }

    private List<FriendRequest> getUnprocessedFriendRequestsSent(User user, Long newFriendId) throws InvalidFriendRequestException {
        List<FriendRequest> friendRequests = user.getFriendRequestsSent().stream()
                .filter(request -> request.getNewFriend().getId().equals(newFriendId))
                .toList();
        if (friendRequests.isEmpty()) {
            throw new InvalidFriendRequestException(String.format(
                    "Friend request between users with ids: %d and %d doesn't exist",
                    user.getId(), newFriendId));
        }

        List<FriendRequest> unprocessedFriendRequestsReceived = friendRequests.stream()
                .filter(request -> request.getStatus().equals(SENT))
                .toList();

        if (unprocessedFriendRequestsReceived.isEmpty()) {
            throw new InvalidFriendRequestException(String.format(
                    "Friend request between users with ids: %d and %d " +
                            "has already been processed",
                    user.getId(), newFriendId));
        }
        return unprocessedFriendRequestsReceived;
    }

    public List<FriendRequestDTO> getAllFriendRequestsReceived(String username) throws InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        Set<FriendRequest> friendRequestsReceived = user.getFriendRequestsReceived();
        return friendRequestsReceived.stream()
                .map(FriendRequestDTO::map)
                .toList();
    }
}
