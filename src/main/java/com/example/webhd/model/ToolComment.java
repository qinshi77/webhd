// ToolComment.java
package com.example.webhd.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ToolComment {
    private Integer id;
    private Integer toolId;
    private String user;
    private String avatar;
    private String content;
    private LocalDateTime time;
    private LocalDateTime createdAt;
}