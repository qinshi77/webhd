package com.example.webhd.service;

import com.example.webhd.dto.PostCreateDTO;
import com.example.webhd.mapper.FollowMapper;
import com.example.webhd.mapper.LikeMapper;
import com.example.webhd.mapper.PostMapper;
import com.example.webhd.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FollowMapper followMapper;  // 注入 FollowMapper

    /**
     * 获取所有帖子（带点赞状态和关注状态）
     * @param currentUserId 当前登录用户ID（可能为null）
     */
    public List<Map<String, Object>> getPostList(Long currentUserId) {
        // 1. 获取帖子基本信息
        List<Map<String, Object>> posts = postMapper.getPostsBasic();

        // 2. 为每个帖子组装author、comments、点赞状态和关注状态
        for (Map<String, Object> post : posts) {
            // 获取作者ID
            Long authorId = (Long) post.get("author_id");
            if (authorId == null) {
                authorId = (Long) post.get("authorId");
            }

            // 组装author对象
            Map<String, Object> author = new HashMap<>();
            author.put("username", post.remove("username"));
            author.put("avatar", post.remove("avatar"));
            author.put("id", authorId);  // 添加作者ID到author对象
            post.put("author", author);

            // 获取该帖子的评论
            Long postId = (Long) post.get("id");
            List<Map<String, Object>> comments = postMapper.getCommentsByPostId(postId);

            // 处理评论的author格式
            List<Map<String, Object>> formattedComments = new ArrayList<>();
            for (Map<String, Object> comment : comments) {
                Map<String, Object> commentAuthor = new HashMap<>();
                commentAuthor.put("username", comment.remove("authorUsername"));
                commentAuthor.put("avatar", comment.remove("authorAvatar"));

                Map<String, Object> formattedComment = new HashMap<>();
                formattedComment.put("author", commentAuthor);
                formattedComment.put("content", comment.get("content"));
                formattedComment.put("createdAt", comment.get("createdAt"));

                formattedComments.add(formattedComment);
            }
            post.put("comments", formattedComments);

            // 判断当前用户是否点赞
            if (currentUserId != null) {
                int liked = likeMapper.checkLike(postId, currentUserId);
                post.put("liked", liked > 0);

                // 判断当前用户是否关注了作者
                int isFollowing = followMapper.checkFollow(currentUserId, authorId);
                post.put("isFollowing", isFollowing > 0);
            } else {
                post.put("liked", false);
                post.put("isFollowing", false);
            }

            post.put("showComments", false);
            post.put("newComment", "");
        }

        return posts;
    }

    /**
     * 重载方法：兼容无参调用（默认未登录）
     */
    public List<Map<String, Object>> getPostList() {
        return getPostList(null);
    }

    /**
     * 发布新文章
     * @param postDTO 文章数据
     * @param authorId 作者ID
     * @return 发布后的文章信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createPost(PostCreateDTO postDTO, Long authorId) {
        // 1. 创建 Post 对象
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setImage(postDTO.getImage());
        post.setAuthorId(authorId);

        // 2. 插入数据库
        int result = postMapper.insertPost(post);

        if (result <= 0) {
            throw new RuntimeException("发布失败");
        }

        // 3. 获取刚插入的帖子ID
        Long postId = post.getId();

        // 4. 获取作者信息（这里简化处理，实际应该从数据库或session获取）
        Map<String, Object> author = new HashMap<>();
        author.put("id", authorId);
        author.put("username", "当前用户");  // 可以从数据库查询
        author.put("avatar", "默认头像");

        // 5. 返回帖子信息
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("id", postId);
        newPost.put("content", post.getContent());
        newPost.put("image", post.getImage());
        newPost.put("createdAt", new Date());
        newPost.put("likes", 0);
        newPost.put("liked", false);
        newPost.put("isFollowing", false);  // 刚发布的帖子，自己关注自己为false
        newPost.put("author", author);
        newPost.put("comments", new ArrayList<>());
        newPost.put("showComments", false);
        newPost.put("newComment", "");

        return newPost;
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param currentUserId 当前登录用户ID
     * @return 帖子详情
     */
    public Map<String, Object> getPostDetail(Long postId, Long currentUserId) {
        // 1. 获取帖子基本信息
        Map<String, Object> post = postMapper.getPostDetailById(postId);

        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 获取作者ID
        Long authorId = (Long) post.get("author_id");

        // 2. 组装author对象
        Map<String, Object> author = new HashMap<>();
        author.put("id", authorId);
        author.put("username", post.remove("authorName"));
        author.put("avatar", post.remove("authorAvatar"));
        post.put("author", author);

        // 3. 获取评论列表
        List<Map<String, Object>> comments = postMapper.getCommentsByPostId(postId);
        List<Map<String, Object>> formattedComments = new ArrayList<>();
        for (Map<String, Object> comment : comments) {
            Map<String, Object> commentAuthor = new HashMap<>();
            commentAuthor.put("username", comment.remove("authorUsername"));
            commentAuthor.put("avatar", comment.remove("authorAvatar"));

            Map<String, Object> formattedComment = new HashMap<>();
            formattedComment.put("id", comment.get("id"));
            formattedComment.put("author", commentAuthor);
            formattedComment.put("content", comment.get("content"));
            formattedComment.put("createdAt", comment.get("createdAt"));

            formattedComments.add(formattedComment);
        }
        post.put("comments", formattedComments);

        // 4. 判断当前用户是否点赞和关注作者
        if (currentUserId != null) {
            int liked = likeMapper.checkLike(postId, currentUserId);
            post.put("liked", liked > 0);

            int isFollowing = followMapper.checkFollow(currentUserId, authorId);
            post.put("isFollowing", isFollowing > 0);
        } else {
            post.put("liked", false);
            post.put("isFollowing", false);
        }

        // 5. 添加前端需要的字段
        post.put("showComments", false);
        post.put("newComment", "");

        return post;
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Long postId, Long userId) {
        // 1. 检查帖子是否存在
        Map<String, Object> post = postMapper.getPostBasicById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 2. 检查是否有权限删除（只有作者可以删除）
        Long authorId = ((Number) post.get("author_id")).longValue();
        if (!authorId.equals(userId)) {
            throw new RuntimeException("无权限删除此帖子");
        }

        // 3. 删除帖子（评论和点赞会通过外键级联删除）
        int result = postMapper.deletePost(postId, userId);

        return result > 0;
    }

    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @param content 新内容
     * @param image 新图片URL
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(Long postId, Long userId, String content, String image) {
        // 1. 检查帖子是否存在
        Map<String, Object> post = postMapper.getPostBasicById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 2. 检查是否有权限修改（只有作者可以修改）
        Long authorId = ((Number) post.get("author_id")).longValue();
        if (!authorId.equals(userId)) {
            throw new RuntimeException("无权限修改此帖子");
        }

        // 3. 更新帖子
        Post updatePost = new Post();
        updatePost.setId(postId);
        updatePost.setContent(content);
        updatePost.setImage(image);
        updatePost.setAuthorId(userId);

        int result = postMapper.updatePost(updatePost);

        return result > 0;
    }

    /**
     * 获取用户的所有帖子
     * @param userId 用户ID
     * @param currentUserId 当前登录用户ID（用于判断点赞状态和关注状态）
     * @return 帖子列表
     */
    public List<Map<String, Object>> getUserPosts(Long userId, Long currentUserId) {
        // 1. 获取用户的所有帖子
        List<Map<String, Object>> posts = postMapper.getPostsByUserId(userId);

        // 2. 为每个帖子添加点赞状态和关注状态
        for (Map<String, Object> post : posts) {
            Long postId = (Long) post.get("id");

            if (currentUserId != null) {
                int liked = likeMapper.checkLike(postId, currentUserId);
                post.put("liked", liked > 0);

                // 判断当前用户是否关注了该作者
                int isFollowing = followMapper.checkFollow(currentUserId, userId);
                post.put("isFollowing", isFollowing > 0);
            } else {
                post.put("liked", false);
                post.put("isFollowing", false);
            }

            post.put("showComments", false);
            post.put("newComment", "");
        }

        return posts;
    }

    /**
     * 搜索帖子
     * @param keyword 搜索关键词
     * @param currentUserId 当前登录用户ID
     * @return 帖子列表
     */
    public List<Map<String, Object>> searchPosts(String keyword, Long currentUserId) {
        // 1. 搜索帖子
        List<Map<String, Object>> posts = postMapper.searchPosts(keyword);

        // 2. 为每个帖子添加点赞状态和关注状态
        for (Map<String, Object> post : posts) {
            // 获取作者ID
            Long authorId = (Long) post.get("author_id");

            // 组装author对象
            Map<String, Object> author = new HashMap<>();
            author.put("id", authorId);
            author.put("username", post.remove("username"));
            author.put("avatar", post.remove("avatar"));
            post.put("author", author);

            Long postId = (Long) post.get("id");

            if (currentUserId != null) {
                int liked = likeMapper.checkLike(postId, currentUserId);
                post.put("liked", liked > 0);

                int isFollowing = followMapper.checkFollow(currentUserId, authorId);
                post.put("isFollowing", isFollowing > 0);
            } else {
                post.put("liked", false);
                post.put("isFollowing", false);
            }

            post.put("comments", new ArrayList<>());
            post.put("showComments", false);
            post.put("newComment", "");
        }

        return posts;
    }

    /**
     * 获取热门帖子
     * @param limit 数量限制
     * @param currentUserId 当前登录用户ID
     * @return 热门帖子列表
     */
    public List<Map<String, Object>> getHotPosts(int limit, Long currentUserId) {
        // 1. 获取热门帖子
        List<Map<String, Object>> posts = postMapper.getHotPosts(limit);

        // 2. 为每个帖子添加点赞状态和关注状态
        for (Map<String, Object> post : posts) {
            // 获取作者ID
            Long authorId = (Long) post.get("author_id");

            // 组装author对象
            Map<String, Object> author = new HashMap<>();
            author.put("id", authorId);
            author.put("username", post.remove("username"));
            author.put("avatar", post.remove("avatar"));
            post.put("author", author);

            Long postId = (Long) post.get("id");

            if (currentUserId != null) {
                int liked = likeMapper.checkLike(postId, currentUserId);
                post.put("liked", liked > 0);

                int isFollowing = followMapper.checkFollow(currentUserId, authorId);
                post.put("isFollowing", isFollowing > 0);
            } else {
                post.put("liked", false);
                post.put("isFollowing", false);
            }

            post.put("comments", new ArrayList<>());
            post.put("showComments", false);
            post.put("newComment", "");
        }

        return posts;
    }

    /**
     * 分页获取帖子
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param currentUserId 当前登录用户ID
     * @return 帖子列表和总数
     */
    public Map<String, Object> getPostsByPage(int page, int size, Long currentUserId) {
        int offset = page * size;

        // 1. 获取分页数据
        List<Map<String, Object>> posts = postMapper.getPostsByPage(offset, size);

        // 2. 获取总数
        int total = postMapper.getPostCount();

        // 3. 处理数据
        for (Map<String, Object> post : posts) {
            // 获取作者ID
            Long authorId = (Long) post.get("author_id");

            // 组装author对象
            Map<String, Object> author = new HashMap<>();
            author.put("id", authorId);
            author.put("username", post.remove("username"));
            author.put("avatar", post.remove("avatar"));
            post.put("author", author);

            Long postId = (Long) post.get("id");

            // 获取评论（只获取前几条用于预览）
            List<Map<String, Object>> comments = postMapper.getCommentsByPostId(postId);
            List<Map<String, Object>> formattedComments = new ArrayList<>();
            // 只取前3条评论作为预览
            int commentLimit = Math.min(3, comments.size());
            for (int i = 0; i < commentLimit; i++) {
                Map<String, Object> comment = comments.get(i);
                Map<String, Object> commentAuthor = new HashMap<>();
                commentAuthor.put("username", comment.remove("authorUsername"));
                commentAuthor.put("avatar", comment.remove("authorAvatar"));

                Map<String, Object> formattedComment = new HashMap<>();
                formattedComment.put("author", commentAuthor);
                formattedComment.put("content", comment.get("content"));
                formattedComment.put("createdAt", comment.get("createdAt"));

                formattedComments.add(formattedComment);
            }
            post.put("comments", formattedComments);

            // 判断点赞状态和关注状态
            if (currentUserId != null) {
                int liked = likeMapper.checkLike(postId, currentUserId);
                post.put("liked", liked > 0);

                int isFollowing = followMapper.checkFollow(currentUserId, authorId);
                post.put("isFollowing", isFollowing > 0);
            } else {
                post.put("liked", false);
                post.put("isFollowing", false);
            }

            post.put("showComments", false);
            post.put("newComment", "");
        }

        // 4. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("posts", posts);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("hasMore", (page + 1) * size < total);

        return result;
    }

    /**
     * 获取帖子数量
     * @return 帖子总数
     */
    public int getPostCount() {
        return postMapper.getPostCount();
    }
}