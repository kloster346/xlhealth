package cn.xlhealth.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import cn.xlhealth.backend.config.properties.FileUploadProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileUploadProperties.class)
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
