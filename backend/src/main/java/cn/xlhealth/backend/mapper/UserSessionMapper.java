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
    @Select("SELECT * FROM user_sessions WHERE user_id = #{userId} AND expires_at > NOW() ORDER BY last_accessed_at DESC")
    List<UserSession> findValidSessionsByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID查询有效会话
     */
    @Select("SELECT * FROM user_sessions WHERE session_id = #{sessionId} AND expires_at > NOW()")
    UserSession findValidSessionById(@Param("sessionId") String sessionId);

    /**
     * 更新会话的最后访问时间
     */
    @Update("UPDATE user_sessions SET last_accessed_at = NOW() WHERE session_id = #{sessionId}")
    int updateLastAccessedTime(@Param("sessionId") String sessionId);

    /**
     * 删除过期会话
     */
    @Delete("DELETE FROM user_sessions WHERE expires_at <= NOW()")
    int deleteExpiredSessions();
}