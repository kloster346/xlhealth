package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

        /**
         * 根据用户名查找用户
         */
        @Select("SELECT * FROM users WHERE username = #{username}")
        User findByUsername(@Param("username") String username);

        /**
         * 根据邮箱查找用户
         */
        @Select("SELECT * FROM users WHERE email = #{email}")
        User findByEmail(@Param("email") String email);

        /**
         * 根据用户名或邮箱查找用户
         */
        @Select("SELECT * FROM users WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
        User findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

        /**
         * 更新用户最后登录时间和IP
         */
        @Update("UPDATE users SET last_login_time = #{lastLoginTime}, last_login_ip = #{lastLoginIp}, updated_time = NOW() WHERE id = #{userId}")
        int updateLastLoginInfo(@Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime,
                        @Param("lastLoginIp") String lastLoginIp);

        /**
         * 更新用户最后登录时间
         */
        @Update("UPDATE users SET last_login_time = #{lastLoginTime}, updated_time = NOW() WHERE id = #{userId}")
        int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime);

        /**
         * 根据用户状态查询用户列表（分页）
         */
        @Select("SELECT * FROM users WHERE status = #{status} AND deleted = 0 ORDER BY created_time DESC")
        IPage<User> findByStatus(Page<User> page, @Param("status") User.UserStatus status);

        /**
         * 统计活跃用户总数
         */
        @Select("SELECT COUNT(*) FROM users WHERE deleted = 0")
        Long countActiveUsers();

        /**
         * 根据注册时间范围查询用户
         */
        @Select("SELECT * FROM users WHERE created_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY created_time DESC")
        List<User> findByRegistrationTimeRange(@Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        /**
         * 检查用户名是否存在
         */
        @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND deleted = 0")
        Long existsByUsername(@Param("username") String username);

        /**
         * 检查邮箱是否存在
         */
        @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND deleted = 0")
        Long existsByEmail(@Param("email") String email);

        /**
         * 检查手机号是否存在
         */
        @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone} AND deleted = 0")
        Long existsByPhone(@Param("phone") String phone);

        /**
         * 逻辑删除用户
         */
        @Update("UPDATE users SET deleted = 1, updated_time = NOW() WHERE id = #{userId}")
        int softDeleteUser(@Param("userId") Long userId);

        // ========== TASK005: 用户管理接口数据访问方法 ==========

        /**
         * 更新用户基本信息
         */
        @Update("UPDATE users SET updated_time = NOW(), email = COALESCE(#{email}, email), nickname = COALESCE(#{nickname}, nickname), avatar_url = COALESCE(#{avatarUrl}, avatar_url) WHERE id = #{userId}")
        int updateUserProfile(@Param("userId") Long userId,
                        @Param("email") String email,
                        @Param("nickname") String nickname,
                        @Param("avatarUrl") String avatarUrl);

        /**
         * 更新用户密码
         */
        @Update("UPDATE users SET password_hash = #{passwordHash}, updated_time = NOW() WHERE id = #{userId}")
        int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

        /**
         * 更新用户头像
         */
        @Update("UPDATE users SET avatar_url = #{avatarUrl}, updated_time = NOW() WHERE id = #{userId}")
        int updateUserAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);
}