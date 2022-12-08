package com.realbeatz.post;

import com.realbeatz.exceptions.InvalidAccessException;
import com.realbeatz.exceptions.InvalidPostIdException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.payloads.requests.CreatePostRequest;
import com.realbeatz.payloads.requests.NewCommentRequest;
import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.post.comment.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.realbeatz.utils.CustomHeaders.USERNAME;
import static com.realbeatz.utils.HttpRequestUtils.getUsernameFromRequest;

@CrossOrigin(origins = "*", originPatterns = "*")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getPostById(
            @PathVariable(name = "postId") Long postId) {

        log.info("Getting post with id: {}", postId);
        PostDTO post;

        try {
            post = postService.getPostById(postId);
        } catch (InvalidPostIdException e) {
            log.error("Error getting post with id: {}", postId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'USER_SUPER_ADMIN')")
    public ResponseEntity<?> createNewPost(
            @RequestBody CreatePostRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        PostDTO newPost;
        try {
            newPost = postService.createNewPost(
                    username,
                    requestBody.getContent(),
                    requestBody.getSongTitle(),
                    requestBody.getArtists());
        } catch (InvalidUserInputException | InvalidUsernameException e) {
            log.error("Error creating new post: {}", requestBody);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity.ok(newPost);
    }

    // todo: implement a way to let super admin edit the post even super admin isn't the original creator
    @PatchMapping("/update/{postId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody Map<String, String> updates,
            HttpServletRequest request) {

        String username = (String) request.getAttribute(USERNAME);

        log.info("Updating post with id: {}, updates: {}", postId, updates);

        PostDTO postDTO;

        try {
            postDTO = postService.updatePost(postId, username, updates);

        } catch (InvalidAccessException e) {
            log.error("User with username: {} is not authorized to update post with id: {}",
                    username, postId);

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorMessage.of(e.getMessage()));

        } catch (Exception e) {
            log.error("Error updating post with id: {}, updates: {}",
                    postId, updates, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(postDTO);
    }

    // todo: delete posts

    // todo: like posts

    // todo: unlike a post


    @GetMapping("/{postId}/comment")
    @PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
    public ResponseEntity<?> getAllCommentsOfPostById(
            @PathVariable(name = "postId") Long postId) {
        log.info("Get all comments of post with id: {}", postId);

        List<CommentDTO> commentDTOList;

        try {
            commentDTOList = postService.getAllCommentsOfPostById(postId);
        } catch (Exception e) {
            log.error("Error getting all comments of post with id: {}", postId, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(commentDTOList);
    }

    //todo: get specific comment on post

    @PostMapping("/{postId}/comment/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> createNewComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody NewCommentRequest requestBody,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);

        log.info("Posting new comment on post with id: {}", postId);

        CommentDTO newCommentDTO;

        try {
            newCommentDTO = postService.createNewComment(
                    username, postId, requestBody.getContent());
        } catch (Exception e) {
            log.error("Error posting new comment on post with id: {}", postId, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(newCommentDTO);
    }

    // todo: update comment

    // todo: delete comment


    @GetMapping("/get-all-related")
    public ResponseEntity<?> fetchAllRelatedPosts(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        log.info("Fetching all posts related to user: {}", username);

        List<PostDTO> postDTOs;

        try {
            postDTOs = postService.getAllPostsRelatedToUser(username);
        } catch (InvalidUsernameException e) {
            log.info("Error fetching all posts related to user: {}", username);
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

        return ResponseEntity.ok(postDTOs);
    }
}
