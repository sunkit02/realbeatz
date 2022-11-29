package com.realbeatz.security.auth;

import com.realbeatz.exceptions.DuplicateUsernameException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.payloads.requests.RegisterUserRequest;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.user.UserDTO;
import com.realbeatz.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // Login is taken care of by JwtUsernameAndPasswordAuthenticationFilter

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(
            @RequestBody RegisterUserRequest request,
            HttpServletRequest servletRequest) {

        log.info("Request ip: {}:{}", servletRequest.getRemoteAddr(), servletRequest.getRemotePort());

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
            log.error("Error processing request: {}, Error Message: '{}'", request, e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }
}
