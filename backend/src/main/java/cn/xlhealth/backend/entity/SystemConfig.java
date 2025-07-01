package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 对应数据库表：system_config
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_config")
public class SystemConfig {

    /**
     * 配置键名
     */
    @TableId(value = "config_key", type = IdType.INPUT)
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 配置类型
     */
    @TableField("config_type")
    private ConfigType configType;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 配置类型枚举
     */
    public enum ConfigType {
        STRING,     // 字符串
        NUMBER,     // 数字
        BOOLEAN,    // 布尔值
        JSON        // JSON对象
    }
}