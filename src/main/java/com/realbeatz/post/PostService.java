package com.realbeatz.post;

import com.realbeatz.exceptions.*;
import com.realbeatz.post.comment.Comment;
import com.realbeatz.post.comment.CommentDTO;
import com.realbeatz.post.comment.CommentRepository;
import com.realbeatz.user.User;
import com.realbeatz.user.UserService;
import com.realbeatz.utils.PostUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static com.realbeatz.utils.ValidationUtils.validateField;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    // input validation checks for all the updatable fields of a post
    private final Map<String, Predicate<Object>> postChecks =
            PostUtils.getPostChecks();
    private final List<String> UPDATABLE_POST_FIELDS =
            List.of("content", "songTitle", "artists");

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostDTO::map)
                .toList();
    }

    public PostDTO getPostById(Long postId) throws InvalidPostIdException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new InvalidPostIdException(
                        "The post with id: " + postId + " doesn't exist"));
        return PostDTO.map(post);
    }

    public PostDTO createNewPost(Long userId,
                                 String content,
                                 String songTitle,
                                 String artists) throws InvalidUserIdException, InvalidUserInputException {
        User user = userService.getUserById(userId);
        return createNewPost(user, content, songTitle, artists);
    }

    public PostDTO createNewPost(String username,
                                 String content,
                                 String songTitle,
                                 String artists) throws InvalidUserInputException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        return createNewPost(user, content, songTitle, artists);
    }

    public PostDTO createNewPost(
            User user,
            String content,
            String songTitle,
            String artists) throws InvalidUserInputException {


        Map<String, String> input = new HashMap<>();
        input.put("content", content);
        input.put("songTitle", songTitle);
        input.put("artists", artists);
        for (Map.Entry<String, String> entry : input.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            validateField(field, value, postChecks);
        }

        Post post = Post.builder()
                .creator(user)
                .content(content)
                .artists(artists)
                .songTitle(songTitle)
                .postTime(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return PostDTO.map(post);
    }

    public List<CommentDTO> getAllCommentsOfPostById(Long postId) throws Exception {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post with id: " + postId + " doesn't exist"));
        return post.getComments().stream()
                .map(CommentDTO::map)
                .toList();
    }

    public CommentDTO createNewComment(Long userId,
                                       Long postId,
                                       String content) throws InvalidUserIdException, InvalidPostIdException {
        User user = userService.getUserById(userId);

        return createNewComment(user, postId, content);
    }

    public CommentDTO createNewComment(String username,
                                       Long postId,
                                       String content) throws InvalidPostIdException, InvalidUsernameException {
        User user = userService.getUserByUsername(username);

        return createNewComment(user, postId, content);
    }
    // todo: validate user input
    public CommentDTO createNewComment(User user,
                                       Long postId,
                                       String content) throws InvalidPostIdException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new InvalidPostIdException(
                        "Post with id: " + postId + " doesn't exist"));

        // create comment
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .timePosted(LocalDateTime.now())
                .build();

        // add comment to post
        post.getComments().add(comment);

        commentRepository.save(comment);
        postRepository.save(post);

        return CommentDTO.map(comment);
    }

    public PostDTO updatePost(Long postId, 
                              Long userId,
                              Map<String, String> updates) throws InvalidPostIdException, InvalidUserInputException, InvalidUserIdException, InvalidAccessException {
        User user = userService.getUserById(userId);
        return updatePost(postId, user, updates);
    }
    
    public PostDTO updatePost(Long postId,
                              String username,
                              Map<String, String> updates) throws InvalidPostIdException, InvalidUserInputException, InvalidUsernameException, InvalidAccessException {
        User user = userService.getUserByUsername(username);
        return updatePost(postId, user, updates);
    }
    
    public PostDTO updatePost(Long postId, User user, Map<String, String> updates) throws InvalidPostIdException, InvalidUserInputException, InvalidAccessException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new InvalidPostIdException(
                        "Post with id: " + postId + " doesn't exist"));
        
        // Ensure the person editing the post is the original creator
        boolean isCreator = post.getCreator().equals(user);
        if (!isCreator) {
            throw new InvalidAccessException(
                    "User with username: " + user.getUsername() +
                            " is not the creator of post with id: " + postId);
        }


        // filter out all fields that are not allowed or doesn't exist
        List<String> validKeys = updates.keySet().stream()
                .filter(UPDATABLE_POST_FIELDS::contains)
                .toList();


        // update values for each field
        for (String key : validKeys) {
            String value = updates.get(key);
            // validate user input against check
            validateField(key, value, postChecks);

            // update post content after passing check
            Field field = ReflectionUtils.findField(Post.class, key);
            Objects.requireNonNull(field).setAccessible(true);
            ReflectionUtils.setField(field, post, value);
        }

        postRepository.save(post);
        return PostDTO.map(post);
    }

    public List<PostDTO> getAllPostsByUser(Long userId) throws InvalidUserIdException {
        User user = userService.getUserById(userId);
        return getAllPostsByUser(user);
    }

    public List<PostDTO> getAllPostsByUser(String username) throws InvalidUsernameException {
        User user = userService.getUserByUsername(username);
        return getAllPostsByUser(user);
    }

    public List<PostDTO> getAllPostsByUser(User user) {
        return postRepository.findPostByCreator(user).stream()
                .map(PostDTO::map)
                .toList();
    }
}
