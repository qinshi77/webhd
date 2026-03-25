package com.example.webhd.service;

import com.example.webhd.mapper.CommentMapper;
import com.example.webhd.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.webhd.model.Comment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    /**
     * 添加评论
     */
    @Transactional
    public Map<String, Object> addComment(Long postId, Long authorId, String content) {
        // 创建 Comment 对象
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setContent(content);

        // 插入评论
        commentMapper.insertComment(comment);

        // 现在可以获取生成的 ID
        Long commentId = comment.getId();
        System.out.println("生成的评论ID: " + commentId);

        // 返回评论信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", commentId);
        result.put("content", content);
        result.put("createdAt", new Date());

        // 获取作者信息
        Map<String, Object> author = new HashMap<>();
        author.put("username", "用户名"); // 需要从数据库查询
        author.put("avatar", "头像");     // 需要从数据库查询
        result.put("author", author);

        return result;
    }

    /**
     * 删除评论
     */
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        int result = commentMapper.deleteComment(commentId, userId);
        return result > 0;
    }

    /**
     * 获取帖子的评论列表
     */
    public List<Map<String, Object>> getPostComments(Long postId) {
        List<Map<String, Object>> comments = commentMapper.getCommentsByPostId(postId);

        // 格式化评论数据
        for (Map<String, Object> comment : comments) {
            Map<String, Object> author = new HashMap<>();
            author.put("username", comment.remove("username"));
            author.put("avatar", comment.remove("avatar"));
            comment.put("author", author);
        }

        return comments;
    }
}