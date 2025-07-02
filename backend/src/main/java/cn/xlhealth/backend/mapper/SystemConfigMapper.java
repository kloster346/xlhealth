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
    @Select("SELECT config_value FROM system_configs WHERE config_key = #{configKey} AND deleted = 0")
    String findValueByKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查询配置列表
     */
    @Select("SELECT * FROM system_configs WHERE config_type = #{configType} AND deleted = 0")
    List<SystemConfig> findByConfigType(@Param("configType") SystemConfig.ConfigType configType);

    /**
     * 根据配置分类查询配置列表
     */
    @Select("SELECT * FROM system_configs WHERE category = #{category} AND deleted = 0 ORDER BY sort_order ASC")
    List<SystemConfig> findByCategory(@Param("category") String category);

    /**
     * 根据配置键更新配置值
     */
    @Update("UPDATE system_configs SET config_value = #{configValue}, updated_time = NOW() WHERE config_key = #{configKey} AND deleted = 0")
    int updateValueByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 根据ID逻辑删除配置
     */
    @Update("UPDATE system_configs SET deleted = 1, updated_time = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}