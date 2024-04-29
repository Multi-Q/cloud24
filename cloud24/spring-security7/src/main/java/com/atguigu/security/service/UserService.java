package com.atguigu.security.service;

import com.atguigu.security.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author QRH
 * @date 2024/4/29 15:03
 * @description TODO
 */
public interface UserService extends IService<User> {
    void saveUserDetails(User user);
}
