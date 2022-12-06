package com.realbeatz.security.auth;

import com.realbeatz.exceptions.DuplicateUsernameException;
import com.realbeatz.exceptions.IllegalFileTypeException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.payloads.requests.LoginRequest;
import com.realbeatz.payloads.requests.RegisterUserRequest;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.user.UserDTO;
import com.realbeatz.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.realbeatz.utils.CustomHeaders.ACCESS_TOKEN;
import static com.realbeatz.utils.CustomHeaders.REFRESH_TOKEN;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // Login is taken care of by JwtUsernameAndPasswordAuthenticationFilter

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> registerNewUser(
            @RequestPart("userProfile") RegisterUserRequest request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            HttpServletRequest servletRequest) {

        log.info("Request ip: {}:{}", servletRequest.getRemoteAddr(), servletRequest.getRemotePort());

        log.info("Request to register user with username: {}", request.getUsername());

        UserDTO userDTO;

        try {
            userDTO = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getLastName(),
                    request.getFirstName(),
                    request.getDob(),
                    request.getBio(),
                    profilePicture);
        } catch (DuplicateUsernameException | InvalidUserInputException | IllegalFileTypeException | IOException e) {
            log.error("Error processing request to register new user with username: {}, Error Message: '{}'",
                    request.getUsername(), e.getMessage(), e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> userLogin(
            @RequestBody LoginRequest loginRequest) {
        log.info("Subject with username: {} is attempting to authenticate.",
                loginRequest.getUsername());
        AuthService.Tokens tokens;
        try {
            tokens = authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());
        } catch(Exception e) {
            log.info("Subject with username: {} failed to authenticate.",
                    loginRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(ACCESS_TOKEN, tokens.accessToken());
        httpHeaders.add(REFRESH_TOKEN, tokens.refreshToken());

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(tokens);
    }
}
