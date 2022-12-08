package com.realbeatz.user.friends;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRequestDTO {
    private Long id;
    private Long requesterId;
    private Long newFriendId;
    private String requesterName;
    private String message;
    private String requesterProfilePictureFullName;
    private FriendRequestStatus status;

    public static FriendRequestDTO map(FriendRequest friendRequest) {
        String requesterName =
                friendRequest.getRequester().getProfile().getFirstName() + " " +
                friendRequest.getRequester().getProfile().getLastName();
        String profilePictureFullName = friendRequest.getRequester().getProfile().getProfilePictureFullName();
        return FriendRequestDTO.builder()
                .id(friendRequest.getId())
                .requesterId(friendRequest.getRequester().getId())
                .requesterName(requesterName)
                .newFriendId(friendRequest.getNewFriend().getId())
                .requesterProfilePictureFullName(profilePictureFullName)
                .message(friendRequest.getMessage())
                .status(friendRequest.getStatus())
                .build();
    }
}
