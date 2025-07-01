package cn.xlhealth.backend.service.impl;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 这里是临时实现，后续需要从数据库中查询用户信息
        // 在TASK003中会完善用户实体类和用户服务
        
        if ("admin".equals(username)) {
            // 创建一个临时的管理员用户用于测试
            return User.builder()
                    .username("admin")
                    .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // password: password
                    .authorities("ROLE_ADMIN")
                    .build();
        }
        
        throw new UsernameNotFoundException("用户不存在: " + username);
    }
}