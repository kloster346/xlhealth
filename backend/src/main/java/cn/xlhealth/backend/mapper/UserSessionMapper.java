package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.UserSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户会话数据访问层
 */
@Mapper
public interface UserSessionMapper extends BaseMapper<UserSession> {

        /**
         * 根据用户ID查询有效会话
         */
        @Select("SELECT * FROM user_sessions WHERE user_id = #{userId} AND expires_at > NOW() AND deleted = 0 AND status = 'ACTIVE' ORDER BY last_activity_time DESC")
        List<UserSession> findValidSessionsByUserId(@Param("userId") Long userId);

        /**
         * 根据会话令牌查询有效会话
         */
        @Select("SELECT * FROM user_sessions WHERE session_token = #{sessionToken} AND expires_at > NOW() AND deleted = 0 AND status = 'ACTIVE'")
        UserSession findValidSessionByToken(@Param("sessionToken") String sessionToken);

        /**
         * 根据会话令牌查询会话（包括已删除的）
         */
        @Select("SELECT * FROM user_sessions WHERE session_token = #{sessionToken}")
        UserSession findSessionByToken(@Param("sessionToken") String sessionToken);

        /**
         * 更新会话的最后活动时间
         */
        @Update("UPDATE user_sessions SET last_activity_time = NOW() WHERE session_token = #{sessionToken}")
        int updateLastAccessedTime(@Param("sessionToken") String sessionToken);

        /**
         * 更新会话令牌和相关信息
         */
        @Update("UPDATE user_sessions SET session_token = #{newToken}, expires_at = #{expiresAt}, last_activity_time = NOW() WHERE session_token = #{oldToken}")
        int updateSessionToken(@Param("oldToken") String oldToken, @Param("newToken") String newToken,
                        @Param("expiresAt") LocalDateTime expiresAt);

        /**
         * 根据用户ID查询会话列表
         */
        @Select("SELECT * FROM user_sessions WHERE user_id = #{userId} AND status = 'ACTIVE' AND deleted = 0 ORDER BY created_time DESC")
        List<UserSession> findByUserId(@Param("userId") Long userId);

        /**
         * 根据会话令牌查询会话
         */
        @Select("SELECT * FROM user_sessions WHERE session_token = #{sessionToken} AND status = 'ACTIVE' AND deleted = 0")
        UserSession findBySessionToken(@Param("sessionToken") String sessionToken);

        /**
         * 根据刷新令牌查询会话
         */
        @Select("SELECT * FROM user_sessions WHERE refresh_token = #{refreshToken} AND status = 'ACTIVE' AND deleted = 0")
        UserSession findByRefreshToken(@Param("refreshToken") String refreshToken);

        /**
         * 更新会话最后活动时间
         */
        @Update("UPDATE user_sessions SET last_activity_time = #{lastActivityTime}, updated_time = NOW() WHERE id = #{sessionId}")
        int updateLastActivityTime(@Param("sessionId") Long sessionId,
                        @Param("lastActivityTime") LocalDateTime lastActivityTime);

        /**
         * 使会话失效
         */
        @Update("UPDATE user_sessions SET status = 'INVALID', updated_time = NOW() WHERE id = #{sessionId}")
        int expireSession(@Param("sessionId") Long sessionId);

        /**
         * 使用户的所有会话失效
         */
        @Update("UPDATE user_sessions SET status = 'INVALID', updated_time = NOW() WHERE user_id = #{userId} AND status = 'ACTIVE'")
        int expireAllUserSessions(@Param("userId") Long userId);

        /**
         * 逻辑删除过期的会话
         */
        @Update("UPDATE user_sessions SET deleted = 1, updated_time = NOW() WHERE expires_at < #{currentTime} OR status = 'INVALID'")
        int deleteExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

        /**
         * 统计用户活跃会话数
         */
        @Select("SELECT COUNT(*) FROM user_sessions WHERE user_id = #{userId} AND status = 'ACTIVE' AND expires_at > #{currentTime} AND deleted = 0")
        Long countActiveSessionsByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);
}