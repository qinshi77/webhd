package com.example.webhd.dto;

import javax.validation.constraints.NotBlank;

public class CommentCreateDTO {

    // 移除 toolId 字段，因为它来自路径参数
    // @NotNull(message = "工具ID不能为空")  // 删除这行
    // private Integer toolId;  // 删除这行

    @NotBlank(message = "评论内容不能为空")
    private String content;

    // getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}