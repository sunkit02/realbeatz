package com.realbeatz.user;

import com.realbeatz.exception.InvalidDeleteFriendException;
import com.realbeatz.exception.InvalidFriendRequestException;
import com.realbeatz.exception.InvalidUserIdException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserFriendsService {

    private final UserService userService;
    private final UserRepository userRepository;
    public List<UserDTO> getAllFriendsByUserId(Long userId) throws InvalidUserIdException {
        User user = userService.getUserById(userId);
        return user.getFriends().stream()
                .map(UserDTO::map)
                .toList();
    }

    public void addNewFriend(Long userId, Long friendId) throws InvalidUserIdException, InvalidFriendRequestException {
        // check if user is adding oneself as friend
        if (userId.equals(friendId)) {
            throw new InvalidUserIdException(
                    "User cannot add oneself as friend");
        }

        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);

        Set<User> userFriends = user.getFriends();
        Set<User> friendFriends = friend.getFriends();

        // check if friendship has already been established
        if (userFriends.contains(friend)) {
            throw new InvalidFriendRequestException(
                    "User with id: " + userId +
                            " already is friends with id : " + friendId);
        }
        if (friendFriends.contains(user)) {
            throw new InvalidFriendRequestException(
                    "User with id: " + friendId +
                            " already is friends with id : " + userId);
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

        Set<User> userFriends = user.getFriends();
        Set<User> friendFriends = friend.getFriends();

        // check if friendship exists
        if (!userFriends.contains(friend)) {
            throw new InvalidDeleteFriendException(
                    "User with id: " + userId + " doesn't have a friend " +
                            "with id: " + friendId);
        }
        if (!friendFriends.contains(user)) {
            throw new InvalidDeleteFriendException(
                    "User with id: " + userId + " doesn't have a friend " +
                            "with id: " + friendId);
        }


        userFriends.remove(friend);
        friendFriends.remove(user);

        userService.save(user);
        userService.save(friend);
    }
}
