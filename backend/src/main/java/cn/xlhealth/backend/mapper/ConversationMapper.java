package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.Conversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话Mapper接口
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
    // 基础的CRUD操作由BaseMapper提供
}