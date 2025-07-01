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
    @Select("SELECT * FROM user_sessions WHERE user_id = #{userId} AND expires_at > NOW() AND deleted = 0 AND status = 1 ORDER BY last_activity_time DESC")
    List<UserSession> findValidSessionsByUserId(@Param("userId") Long userId);

    /**
     * 根据会话令牌查询有效会话
     */
    @Select("SELECT * FROM user_sessions WHERE session_token = #{sessionToken} AND expires_at > NOW() AND deleted = 0 AND status = 1")
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
    int updateSessionToken(@Param("oldToken") String oldToken, @Param("newToken") String newToken, @Param("expiresAt") LocalDateTime expiresAt);

    /**
     * 删除过期会话
     */
    @Delete("DELETE FROM user_sessions WHERE expires_at <= NOW()")
    int deleteExpiredSessions();
}