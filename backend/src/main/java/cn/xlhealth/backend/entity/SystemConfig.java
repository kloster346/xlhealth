package cn.xlhealth.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 对应数据库表：system_configs
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_configs")
public class SystemConfig {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键名
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置分类
     */
    @TableField("category")
    private String category;

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
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 是否可编辑
     */
    @TableField("is_editable")
    private Boolean isEditable;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

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