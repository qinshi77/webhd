// KitchenTool.java
package com.example.webhd.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class KitchenTool {
    private Integer id;
    private String name;
    private String description;
    private String use;
    private String cuisine;
    private String usage;
    private String buyLink;
    private String image;
    private Integer likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联评论列表
    private List<ToolComment> comments;
}