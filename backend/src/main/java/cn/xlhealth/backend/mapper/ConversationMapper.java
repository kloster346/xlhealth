package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.Conversation;
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
 * 对话Mapper接口
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
    // 基础的CRUD操作由BaseMapper提供

    /**
     * 根据用户ID查询对话列表（分页）
     */
    @Select("SELECT * FROM conversations WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_time DESC")
    IPage<Conversation> findByUserId(Page<Conversation> page, @Param("userId") Long userId);

    /**
     * 根据对话状态查询对话列表
     */
    @Select("SELECT * FROM conversations WHERE status = #{status} AND deleted = 0 ORDER BY created_time DESC")
    List<Conversation> findByStatus(@Param("status") Conversation.ConversationStatus status);

    /**
     * 根据用户ID和对话状态查询对话列表
     */
    @Select("SELECT * FROM conversations WHERE user_id = #{userId} AND status = #{status} AND deleted = 0 ORDER BY created_time DESC")
    List<Conversation> findByUserIdAndStatus(@Param("userId") Long userId,
            @Param("status") Conversation.ConversationStatus status);

    /**
     * 统计用户的对话总数
     */
    @Select("SELECT COUNT(*) FROM conversations WHERE user_id = #{userId} AND deleted = 0")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 根据时间范围查询对话
     */
    @Select("SELECT * FROM conversations WHERE created_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY created_time DESC")
    List<Conversation> findByTimeRange(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 更新对话状态
     */
    @Update("UPDATE conversations SET status = #{status}, updated_time = NOW() WHERE id = #{conversationId}")
    int updateStatus(@Param("conversationId") Long conversationId,
            @Param("status") Conversation.ConversationStatus status);

    /**
     * 更新对话统计信息
     */
    @Update("UPDATE conversations SET message_count = #{messageCount}, total_tokens = #{totalTokens}, updated_time = NOW() WHERE id = #{conversationId}")
    int updateStatistics(@Param("conversationId") Long conversationId, @Param("messageCount") Integer messageCount,
            @Param("totalTokens") Integer totalTokens);

    /**
     * 逻辑删除对话
     */
    @Update("UPDATE conversations SET deleted = 1, updated_time = NOW() WHERE id = #{conversationId}")
    int softDelete(@Param("conversationId") Long conversationId);
}