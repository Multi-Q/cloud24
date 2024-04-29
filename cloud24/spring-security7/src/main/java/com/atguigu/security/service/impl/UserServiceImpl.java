package com.atguigu.security.service.impl;


import com.atguigu.security.config.DBUserDetailManager;
import com.atguigu.security.entity.User;
import com.atguigu.security.mapper.UserMapper;
import com.atguigu.security.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用于练习spring security demo用的表 服务实现类
 * </p>
 *
 * @author QRH
 * @since 2024-04-29
 */
@Service
public class  UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private DBUserDetailManager dbUserDetailManager;


    @Override
    public void saveUserDetails(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(
                        PasswordEncoderFactories.createDelegatingPasswordEncoder()
                                .encode(user.getPassword())
                )
                .roles("USER")
                .build();

        dbUserDetailManager.createUser(userDetails);
    }
}
