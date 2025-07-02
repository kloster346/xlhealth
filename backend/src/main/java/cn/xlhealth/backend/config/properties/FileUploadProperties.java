package cn.xlhealth.backend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 文件存储路径
     */
    private String path = "c:/Users/34682/Desktop/xlhealth/uploads/";

    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,jpeg,png,gif,bmp,webp";

    /**
     * 最大文件大小（字节）
     */
    private long maxSize = 10 * 1024 * 1024; // 10MB

    /**
     * 头像存储子目录
     */
    private String avatarDir = "avatars";

    /**
     * 文件访问URL前缀
     */
    private String urlPrefix = "/api/v1/files";
}