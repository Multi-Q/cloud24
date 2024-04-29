package com.atguigu.security.controller;

import com.atguigu.security.entity.User;
import com.atguigu.security.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author QRH
 * @date 2024/4/29 15:10
 * @description TODO
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/list")
    public List<User> userList(){
        return userService.list();
    }

    @PostMapping("/add")
    public void addUser(@RequestBody User user){
        userService.saveUserDetails(user);
    }

}
