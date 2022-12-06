package com.realbeatz.user.friends;

import com.realbeatz.user.User;
import com.realbeatz.user.profile.UserProfile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendDTO {
    Long id;
    String username;
    String firstName;
    String lastName;
    String bio;
    String profilePictureFullName;


    public static FriendDTO map(User user) {
        UserProfile profile = user.getProfile();
        return FriendDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .profilePictureFullName(profile.getProfilePictureFullName())
                .build();
    }
}
