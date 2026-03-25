// FollowDTO.java
package com.example.webhd.dto;

import javax.validation.constraints.NotNull;

public class FollowDTO {

    @NotNull(message = "被关注者ID不能为空")
    private Long followingId;

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}