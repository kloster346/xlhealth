package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.AuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志数据访问层
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据用户ID查询审计日志（分页）
     */
    @Select("SELECT * FROM audit_logs WHERE user_id = #{userId} ORDER BY created_time DESC")
    IPage<AuditLog> findByUserId(Page<AuditLog> page, @Param("userId") Long userId);

    /**
     * 根据操作类型查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE action = #{action} ORDER BY created_time DESC")
    List<AuditLog> findByAction(@Param("action") String action);

    /**
     * 根据时间范围查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE created_time BETWEEN #{startTime} AND #{endTime} ORDER BY created_time DESC")
    List<AuditLog> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据资源类型查询审计日志
     */
    @Select("SELECT * FROM audit_logs WHERE resource_type = #{resourceType} ORDER BY created_time DESC")
    List<AuditLog> findByResourceType(@Param("resourceType") String resourceType);

    /**
     * 删除指定时间之前的审计日志
     */
    @Delete("DELETE FROM audit_logs WHERE created_time < #{beforeTime}")
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
}