package com.realbeatz.user.friends;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRequestDTO {
    private Long id;
    private Long requesterId;
    private Long newFriendId;
    private String message;
    private FriendRequestStatus status;

    public static FriendRequestDTO map(FriendRequest friendRequest) {
        return FriendRequestDTO.builder()
                .id(friendRequest.getId())
                .requesterId(friendRequest.getRequester().getId())
                .newFriendId(friendRequest.getNewFriend().getId())
                .message(friendRequest.getMessage())
                .status(friendRequest.getStatus())
                .build();
    }
}
