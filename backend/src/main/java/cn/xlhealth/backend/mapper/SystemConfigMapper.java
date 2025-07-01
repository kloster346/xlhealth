package cn.xlhealth.backend.mapper;

import cn.xlhealth.backend.entity.SystemConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统配置数据访问层
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置值
     */
    @Select("SELECT config_value FROM system_config WHERE config_key = #{configKey}")
    String findValueByKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查询配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_type = #{configType} ORDER BY config_key")
    List<SystemConfig> findByConfigType(@Param("configType") String configType);

    /**
     * 更新配置值
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, updated_at = NOW() WHERE config_key = #{configKey}")
    int updateValueByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 检查配置键是否存在
     */
    @Select("SELECT COUNT(*) FROM system_config WHERE config_key = #{configKey}")
    Long existsByKey(@Param("configKey") String configKey);
}