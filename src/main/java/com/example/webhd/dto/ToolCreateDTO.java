package com.example.webhd.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ToolCreateDTO {

    @NotBlank(message = "工具名称不能为空")
    @Size(max = 100, message = "工具名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "工具描述不能超过500个字符")
    private String description;

    @Size(max = 500, message = "使用场景不能超过500个字符")
    private String use;

    @Size(max = 255, message = "适用菜系不能超过255个字符")
    private String cuisine;

    @Size(max = 500, message = "使用方法不能超过500个字符")
    private String usage;

    @Size(max = 255, message = "购买链接不能超过255个字符")
    private String buyLink;

    @Size(max = 255, message = "图片链接不能超过255个字符")
    private String image;

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}