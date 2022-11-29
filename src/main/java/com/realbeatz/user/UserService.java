package com.realbeatz.user;

import com.realbeatz.exceptions.DuplicateUsernameException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.security.auth.AuthUserDetails;
import com.realbeatz.user.profile.UserProfile;
import com.realbeatz.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static com.realbeatz.security.auth.roles.UserRole.USER;
import static com.realbeatz.utils.ValidationUtils.validateField;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<String> UPDATABLE_USER_FIELDS =
            List.of("username", "password");
    private final List<String> UPDATABLE_USER_PROFILE_FIELDS =
            List.of("lastName", "firstName", "dob", "bio");

    private final Map<String, Predicate<Object>> userAccountChecks =
            UserUtils.getUserAccountChecks();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUserDTOs() {
        return userRepository.findAll().stream()
                .map(UserDTO::map)
                .toList();
    }

    public User getUserById(Long userId) throws InvalidUserIdException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserIdException(
                        "User with id: " + userId + " doesn't exist"
                ));
    }

    public UserDTO getUserDTOById(Long userId) throws InvalidUserIdException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new InvalidUserIdException(
                        "User with id: " + userId + " doesn't exist"));
        return UserDTO.map(user);
    }

    public User getUserByUsername(String username) throws InvalidUsernameException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException(
                        "User with username: " + username + " doesn't exist"));
    }

    public UserDTO getUserDTObyUsername(String username) throws InvalidUsernameException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException(
                        "User with username: " + username + " doesn't exist"));

        return UserDTO.map(user);
    }

    public UserDTO registerUser(
            String username,
            String password,
            String lastName,
            String firstName,
            LocalDate dob,
            String bio) throws DuplicateUsernameException, InvalidUserInputException {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(
                    "Username: " + username + " is already taken");
        }

        Map<String, Object> userInputs = new HashMap<>();
        userInputs.put("username", username);
        userInputs.put("password", password);
        userInputs.put("lastName", lastName);
        userInputs.put("firstName", firstName);
        userInputs.put("dob", dob);
        userInputs.put("bio", bio);

        for (Map.Entry<String, Object> entry : userInputs.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            validateField(field, value, userAccountChecks);
        }


        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .registrationDate(LocalDate.now())
                .role(USER)
                .build();

        UserProfile newUserProfile = UserProfile.builder()
                .user(newUser)
                .lastName(lastName)
                .firstName(firstName)
                .dob(dob)
                .bio((bio == null) ? "" : bio)
                .build();

        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .user(newUser)
                .build();

        newUser.setProfile(newUserProfile);
        newUser.setAuthUserDetails(authUserDetails);

        userRepository.save(newUser);
        return UserDTO.map(newUser);
    }

    public void deleteUser(Long userId) throws InvalidUserIdException {
        if (!userRepository.existsById(userId)) {
            throw new InvalidUserIdException(
                    "User with id: " + userId + " doesn't exist");
        }
        userRepository.deleteById(userId);
    }

    public void deleteUser(String username) throws InvalidUserIdException {
        if (!userRepository.existsByUsername(username)) {
            throw new InvalidUserIdException(
                    "User with username: " + username + " doesn't exist");
        }
        userRepository.deleteByUsername(username);
    }

    public UserDTO updateUser(String username, Map<String, String> updates) throws InvalidUsernameException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException(
                        "User with username: " + username + " doesn't exist"));

        return updateUser(user, updates);
    }

    public UserDTO updateUser(Long userId, Map<String, String> updates) throws InvalidUserIdException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserIdException(
                        "User with id: " + userId + " doesn't exist"));

        return updateUser(user, updates);
    }

    public UserDTO updateUser(User user, Map<String, String> updates) {

        // filter out all fields that are not allowed or doesn't exist
        List<String> validKeys = updates.keySet().stream()
                .filter(UPDATABLE_USER_FIELDS::contains)
                .toList();

        // update values for each field
        validKeys.forEach(key -> {
            Field field = ReflectionUtils.findField(User.class, key);
            Objects.requireNonNull(field).setAccessible(true);

            String value = updates.get(key);
            if (key.equals("password")) {
                value = passwordEncoder.encode(value);
            }
            ReflectionUtils.setField(field, user, value);
        });

        userRepository.save(user);
        return UserDTO.map(user);
    }

    public UserDTO updateUserProfile(String username, Map<String, String> updates) throws InvalidUsernameException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException(
                        "User with username: " + username + " doesn't exist"));

        return updateUserProfile(user, updates);
    }

    public UserDTO updateUserProfile(Long userId, Map<String, String> updates) throws InvalidUserIdException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserIdException(
                        "User with id: " + userId + " doesn't exist"));

        return updateUserProfile(user, updates);
    }

    public UserDTO updateUserProfile(User user, Map<String, String> updates) {

        UserProfile profile = user.getProfile();

        // filter out all fields that are not allowed or doesn't exist
        List<String> validKeys = updates.keySet().stream()
                .filter(UPDATABLE_USER_PROFILE_FIELDS::contains)
                .toList();

        // update values for each field
        validKeys.forEach(key -> {
            Field field = ReflectionUtils.findField(UserProfile.class, key);
            Objects.requireNonNull(field).setAccessible(true);
            String value = updates.get(key);
            // todo: make this look less disgusting
            if (key.equals("dob")) {
                ReflectionUtils.setField(field, profile, LocalDate.parse(value));
            } else {
                ReflectionUtils.setField(field, profile, value);
            }
        });

        userRepository.save(user);
        return UserDTO.map(user);
    }


    @SuppressWarnings("all")
    public User save(User user) {
        return userRepository.save(user);
    }
}
