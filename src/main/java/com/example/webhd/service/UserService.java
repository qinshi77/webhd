package com.example.webhd.service;

import com.example.webhd.dto.PasswordUpdateDTO;
import com.example.webhd.mapper.TUserMapper;
import com.example.webhd.mapper.UserDetailMapper;
import com.example.webhd.mapper.UserMapper;
import com.example.webhd.model.TUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.webhd.dto.UserUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean login(String username,String password){
        return userMapper.checkUser(username,password)  > 0;
    }

    @Autowired
    private TUserMapper tUserMapper;

    @Autowired
    private UserDetailMapper userDetailMapper;

    /**
     * 根据用户名获取用户信息
     */
    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> userInfo = userDetailMapper.getUserInfoByUsername(username);
        if (userInfo == null) {
            // 返回默认用户信息
            userInfo = new HashMap<>();
            userInfo.put("username", "美食爱好者");
            userInfo.put("avatar", "https://randomuser.me/api/portraits/men/32.jpg");
            userInfo.put("bio", "热爱美食，喜欢分享各种美食体验");
            userInfo.put("posts", 12);
            userInfo.put("followers", 156);
            userInfo.put("following", 89);
        }
        return userInfo;
    }

    /**
     * 获取当前登录用户信息
     */
    public Map<String, Object> getCurrentUserInfo(Long userId) {
        if (userId == null) {
            return getDefaultUserInfo();
        }
        Map<String, Object> userInfo = userDetailMapper.getCurrentUserInfo(userId);
        return userInfo != null ? userInfo : getDefaultUserInfo();
    }

    /**
     * 默认用户信息
     */
    private Map<String, Object> getDefaultUserInfo() {
        Map<String, Object> defaultInfo = new HashMap<>();
        defaultInfo.put("username", "美食爱好者");
        defaultInfo.put("avatar", "https://randomuser.me/api/portraits/men/32.jpg");
        defaultInfo.put("bio", "热爱美食，喜欢分享各种美食体验");
        defaultInfo.put("posts", 12);
        defaultInfo.put("followers", 156);
        defaultInfo.put("following", 89);
        return defaultInfo;
    }

    /**
     * 更新用户信息（同时修改 username, avatar, bio）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        // 1. 检查用户是否存在
        Map<String, Object> existingUser = userMapper.getUserById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户名是否已被其他用户使用
        String oldUsername = (String) existingUser.get("username");
        String newUsername = updateDTO.getUsername();

        if (!oldUsername.equals(newUsername)) {
            int count = userMapper.checkUsernameExists2(newUsername, userId);
            if (count > 0) {
                throw new RuntimeException("用户名已被使用，请更换其他用户名");
            }
        }

        // 3. 执行更新
        int result = userMapper.updateUserInfo(
                userId,
                updateDTO.getUsername(),
                updateDTO.getAvatar(),
                updateDTO.getBio()
        );

        if (result <= 0) {
            throw new RuntimeException("更新失败");
        }

        // 4. 返回更新后的用户信息
        return getUserInfo2(userId);
    }


    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo2(Long userId) {
        if (userId == null) {
            return getDefaultUserInfo();
        }

        Map<String, Object> userInfo = userMapper.getUserById(userId);

        if (userInfo == null) {
            throw new RuntimeException("用户不存在");
        }

        // 格式化返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", userInfo.get("id"));
        result.put("username", userInfo.get("username"));
        result.put("avatar", userInfo.get("avatar"));
        result.put("bio", userInfo.get("bio") != null ? userInfo.get("bio") : "");
        result.put("createdAt", userInfo.get("createdAt"));
        result.put("updatedAt", userInfo.get("updatedAt"));

        return result;
    }

    /**
     * 修改密码（不使用加密，直接明文存储）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updatePassword(Long userId, PasswordUpdateDTO passwordDTO) {
        // 1. 检查新密码和确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }

        // 2. 检查用户是否存在
        TUser tUser = tUserMapper.getUserById(userId);
        if (tUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 验证原密码是否正确（直接对比明文）
        if (!passwordDTO.getOldPassword().equals(tUser.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 4. 直接使用新密码（不加密）
        String newPassword = passwordDTO.getNewPassword();

        // 5. 更新密码
        int result = tUserMapper.updatePassword(userId, newPassword);

        if (result <= 0) {
            throw new RuntimeException("密码修改失败");
        }

        // 6. 返回成功信息
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "密码修改成功");

        return response;
    }


}
