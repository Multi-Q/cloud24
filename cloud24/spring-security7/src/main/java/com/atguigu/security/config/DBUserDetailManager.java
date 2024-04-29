package com.atguigu.security.config;

import com.atguigu.security.entity.User;
import com.atguigu.security.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author QRH
 * @date 2024/4/29 15:29
 * @description TODO
 */
@Component
public class DBUserDetailManager implements UserDetailsManager, UserDetailsPasswordService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }

    /**
     * 向数据库中插入用户信息
     *
     */
    @Override
    public void createUser(UserDetails userDetails) {
        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEnabled(true);
        userMapper.insert(user);

    }

    /**
     * Update the specified user.
     *
     * @param user
     */
    @Override
    public void updateUser(UserDetails user) {

    }

    /**
     * Remove the user with the given login name from the system.
     *
     * @param username
     */
    @Override
    public void deleteUser(String username) {

    }

    /**
     * Modify the current user's password. This should change the user's password in the
     * persistent user repository (database, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    /**
     * Check if a user with the supplied login name exists in the system.
     *
     * @param username
     */
    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);

        User user = userMapper.selectOne(wrapper);

        if (user==null){
            throw new UsernameNotFoundException("用户不存在");
        }else{
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEnabled(),
                    true, //用户账号是否过期
                    true,//用户凭证是否过期
                    true,//用户是否被锁定
                    authorities//权限列表
            );
        }
    }
}
