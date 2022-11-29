package com.realbeatz.post;

import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.exceptions.InvalidPostIdException;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.exceptions.InvalidUserInputException;
import com.realbeatz.post.comment.CommentDTO;
import com.realbeatz.payloads.requests.CreatePostRequest;
import com.realbeatz.payloads.requests.NewCommentRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
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
    public ResponseEntity<?> createNewPost(
            @RequestBody CreatePostRequest request) {
        PostDTO newPost;
        try {
            newPost = postService.createNewPost(
                    request.getUserId(),
                    request.getContent(),
                    request.getSongTitle(),
                    request.getArtists());
        } catch (InvalidUserIdException | InvalidUserInputException e) {
            log.error("Error creating new post: {}", request);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }
        return ResponseEntity.ok(newPost);
    }

    // todo: implement authorization process to ensure the person editing the post is the owner
    @PatchMapping("/update/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody Map<String, String> updates) {

        log.info("Updating post with id: {}, updates: {}", postId, updates);

        PostDTO postDTO;

        try {
            postDTO = postService.updatePost(postId, updates);
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
    public ResponseEntity<?> createNewComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody NewCommentRequest request) {

        log.info("Posting new comment on post with id: {}", postId);

        CommentDTO newCommentDTO;

        try {
            newCommentDTO = postService.createNewComment(
                    request.getUserId(), postId, request.getContent());
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



}
