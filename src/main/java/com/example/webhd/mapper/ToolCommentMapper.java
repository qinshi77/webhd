package com.example.webhd.mapper;

import com.example.webhd.model.ToolComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ToolCommentMapper {

    /**
     * 新增评论
     */
    @Insert("INSERT INTO tool_comments (tool_id, `user`, avatar, content, `time`, created_at) " +
            "VALUES (#{toolId}, #{user}, #{avatar}, #{content}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(ToolComment comment);

    /**
     * 根据工具ID获取评论列表
     */
    @Select("SELECT id, tool_id as toolId, `user`, avatar, content, `time`, created_at " +
            "FROM tool_comments WHERE tool_id = #{toolId} ORDER BY `time` DESC")
    List<ToolComment> getCommentsByToolId(@Param("toolId") Integer toolId);

    /**
     * 根据评论ID获取评论详情
     */
    @Select("SELECT id, tool_id as toolId, `user`, avatar, content, `time`, created_at " +
            "FROM tool_comments WHERE id = #{commentId}")
    ToolComment getCommentById(@Param("commentId") Integer commentId);

    /**
     * 删除评论
     */
    @Delete("DELETE FROM tool_comments WHERE id = #{commentId} AND `user` = #{userName}")
    int deleteComment(@Param("commentId") Integer commentId, @Param("userName") String userName);

    /**
     * 获取工具评论数量
     */
    @Select("SELECT COUNT(*) FROM tool_comments WHERE tool_id = #{toolId}")
    int getCommentCount(@Param("toolId") Integer toolId);
}