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
     * 更新用户最后登录时间
     */
    @Update("UPDATE users SET last_login_time = #{lastLoginTime}, updated_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 根据状态分页查询用户列表
     */
    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY created_time DESC")
    IPage<User> findByStatus(Page<User> page, @Param("status") String status);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    Long countByStatus(@Param("status") String status);
}