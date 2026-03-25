package com.example.webhd.controller;

import com.example.webhd.mapper.UserMapper;
import com.example.webhd.model.Text;
import com.example.webhd.model.User;
import com.example.webhd.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class hello {
    @GetMapping("/hello")
    public String hello(){
        return "你好springboot!";
    }

    @PostMapping("/text1")
    public String text1(Text text){
        System.out.println(text);
        return "post请求！";
    }

    @PostMapping("/upload")
    public String up(String nickname, MultipartFile photo, HttpServletRequest request) throws IOException{
        System.out.println(nickname);
        System.out.println(photo.getOriginalFilename());
        System.out.println(photo.getContentType());
        String path = request.getServletContext().getRealPath("/upload/");
        System.out.println(path);
        saveFile(photo,path);
        return "上传成功！";
    }

    public void saveFile(MultipartFile photo,String path) throws IOException{
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(path+photo.getOriginalFilename());
        photo.transferTo(file);
    }

    @Autowired //注解声明
    private UserMapper userMapper;

    // 查询用户列表，并返回给前端
    @GetMapping("/findUserAll")
    public List userQuery(){
        List<User> list = userMapper.find();
        System.out.println(list);
        return list;
    }

    @Autowired
    private UserService userService;


    @PostMapping("/userLogin")
    public ResponseEntity<String> login(@RequestBody User user){
        int count = userMapper.checkUser(user.getUsername(), user.getPassword());
        System.out.println(user);
        System.out.println(count);
        return count > 0 ? ResponseEntity.ok("success")
                : ResponseEntity.status(401).body("fail");
    }

    // 做了个简单的校验，校验用户名是否唯一
    @PostMapping("/user/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userMapper.checkUsernameExists(user.getUsername()) > 0) {
            return ResponseEntity.status(409).body("用户名已存在");
        }
        userMapper.insertUser(user.getUsername(), user.getPassword());
        userMapper.insertUsers(user.getUsername());
        return ResponseEntity.ok("注册成功");
    }

}
