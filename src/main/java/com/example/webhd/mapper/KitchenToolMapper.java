package com.example.webhd.mapper;

import com.example.webhd.model.KitchenTool;
import com.example.webhd.model.ToolComment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface KitchenToolMapper {

    /**
     * 查询所有厨房工具
     * 注意：use 和 usage 是 MySQL 保留关键字，需要用反引号包裹
     */
    @Select("SELECT id, name, description, `use`, cuisine, `usage`, " +
            "buy_link as buyLink, image, likes, created_at, updated_at " +
            "FROM kitchen_tools ORDER BY id")
    List<KitchenTool> findAllTools();

    /**
     * 根据工具ID查询评论
     * 注意：time 是 MySQL 保留关键字，需要用反引号包裹
     */
    @Select("SELECT id, tool_id as toolId, `user`, avatar, content, `time`, created_at " +
            "FROM tool_comments WHERE tool_id = #{toolId} ORDER BY `time` DESC")
    List<ToolComment> findCommentsByToolId(@Param("toolId") Integer toolId);

    /**
     * 批量查询所有工具的评论（优化性能）
     * 注意：time 和 user 是 MySQL 保留关键字，需要用反引号包裹
     */
    @Select("SELECT id, tool_id as toolId, `user`, avatar, content, `time`, created_at " +
            "FROM tool_comments ORDER BY `time` DESC")
    List<ToolComment> findAllComments();

    /**
     * 获取工具基本信息
     * 注意：use 和 usage 是 MySQL 保留关键字，需要用反引号包裹
     */
    @Select("SELECT id, name, description, `use`, cuisine, `usage`, " +
            "buy_link, image, likes, created_at " +
            "FROM kitchen_tools WHERE id = #{toolId}")
    Map<String, Object> getToolById(@Param("toolId") Integer toolId);

    /**
     * 更新工具点赞数
     */
    @Update("UPDATE kitchen_tools SET likes = likes + 1 WHERE id = #{toolId}")
    int incrementLikeCount(@Param("toolId") Integer toolId);

    /**
     * 减少工具点赞数
     */
    @Update("UPDATE kitchen_tools SET likes = likes - 1 WHERE id = #{toolId} AND likes > 0")
    int decrementLikeCount(@Param("toolId") Integer toolId);

    /**
     * 新增厨房工具
     */
    @Insert("INSERT INTO kitchen_tools (name, description, `use`, cuisine, `usage`, " +
            "buy_link, image, likes, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{use}, #{cuisine}, #{usage}, " +
            "#{buyLink}, #{image}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTool(KitchenTool tool);

    /**
     * 更新厨房工具
     */
    @Update("UPDATE kitchen_tools SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "`use` = #{use}, " +
            "cuisine = #{cuisine}, " +
            "`usage` = #{usage}, " +
            "buy_link = #{buyLink}, " +
            "image = #{image}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateTool(KitchenTool tool);

    /**
     * 删除厨房工具
     */
    @Delete("DELETE FROM kitchen_tools WHERE id = #{id}")
    int deleteTool(@Param("id") Integer id);

    /**
     * 根据ID查询厨房工具
     */
    @Select("SELECT id, name, description, `use`, cuisine, `usage`, " +
            "buy_link as buyLink, image, likes, created_at, updated_at " +
            "FROM kitchen_tools WHERE id = #{id}")
    KitchenTool findToolById(@Param("id") Integer id);
}