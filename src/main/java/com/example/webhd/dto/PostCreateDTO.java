package com.example.webhd.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PostCreateDTO {

    @NotBlank(message = "内容不能为空")
    @Size(max = 5000, message = "内容不能超过5000字")
    private String content;

    private String image;  // 图片URL，可选

    // getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}