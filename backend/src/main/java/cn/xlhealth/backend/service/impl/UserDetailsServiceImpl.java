package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 * 用于Spring Security认证过程中加载用户信息
 * 注意：这是一个临时实现，后续会在TASK003中完善用户管理功能
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户信息
        cn.xlhealth.backend.entity.User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 根据用户名确定角色权限
        String role = "admin".equals(user.getUsername()) ? "ROLE_ADMIN" : "ROLE_USER";

        // 构建Spring Security的UserDetails对象
        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(role)
                .accountExpired(false)
                .accountLocked(user.getStatus() != cn.xlhealth.backend.entity.User.UserStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(Boolean.TRUE.equals(user.getDeleted()))
                .build();
    }
}