package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 消息数据访问层
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据对话ID查询消息列表（分页）
     */
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY created_at ASC")
    IPage<Message> findByConversationId(Page<Message> page, @Param("conversationId") Long conversationId);

    /**
     * 根据对话ID统计消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE conversation_id = #{conversationId} AND is_deleted = 0")
    Long countByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 查询对话中的最后一条消息
     */
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT 1")
    Message findLastByConversationId(@Param("conversationId") Long conversationId);
}