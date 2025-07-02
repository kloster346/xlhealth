package cn.xlhealth.backend.config;

import cn.xlhealth.backend.config.properties.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器、静态资源等Web相关功能
 * 注意：CORS配置已移至SecurityConfig中统一管理
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径
        String urlPrefix = fileUploadProperties.getUrlPrefix();
        String uploadPath = fileUploadProperties.getPath();

        // 确保路径以/结尾
        if (!urlPrefix.endsWith("/")) {
            urlPrefix += "/";
        }
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        registry.addResourceHandler(urlPrefix + "**")
                .addResourceLocations("file:" + uploadPath);
    }
}