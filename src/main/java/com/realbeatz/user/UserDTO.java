package com.realbeatz.user;

import com.realbeatz.groupchat.GroupChat;
import com.realbeatz.post.PostDTO;
import com.realbeatz.user.profile.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private UserProfile profile;
    private List<String> friends;
    private List<String> groupChats;
    private List<PostDTO> posts;
    private LocalDate registrationDate;

    public static UserDTO map(User user) {
        // todo: change into something that includes user ids
        List<String> friendUsernames = user.getFriends().stream()
                .map(User::getUsername)
                .toList();

        List<String> groupChatNames = user.getGroupChats().stream()
                .map(GroupChat::getChatName)
                .toList();

        List<PostDTO> posts = user.getPosts().stream()
                .map(PostDTO::map)
                .toList();
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .registrationDate(user.getRegistrationDate())
                .profile(user.getProfile())
                .friends(friendUsernames)
                .groupChats(groupChatNames)
                .posts(posts)
                .build();
    }
}
