package cn.xlhealth.backend.service.impl;

import cn.xlhealth.backend.config.properties.FileUploadProperties;
import cn.xlhealth.backend.ui.advice.BusinessException;
import cn.xlhealth.backend.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Override
    public String storeFile(MultipartFile file, String directory) {
        // 验证文件
        validateFile(file);

        try {
            // 创建存储目录
            Path uploadPath = Paths.get(fileUploadProperties.getPath(), directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName() + "." + fileExtension;

            // 存储文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问URL
            String relativePath = directory + "/" + fileName;
            String accessUrl = fileUploadProperties.getUrlPrefix() + "/" + relativePath;

            logger.info("文件上传成功: {}", accessUrl);
            return accessUrl;

        } catch (IOException e) {
            logger.error("文件存储失败", e);
            throw new BusinessException("文件存储失败");
        }
    }

    @Override
    public String storeAvatar(MultipartFile file, Long userId) {
        return storeFile(file, fileUploadProperties.getAvatarDir() + "/" + userId);
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            // 从URL中提取相对路径
            String relativePath = filePath.replace(fileUploadProperties.getUrlPrefix() + "/", "");
            Path path = Paths.get(fileUploadProperties.getPath(), relativePath);

            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("文件删除成功: {}", filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        String[] allowedTypes = fileUploadProperties.getAllowedTypes().split(",");

        return Arrays.asList(allowedTypes).contains(fileExtension);
    }

    @Override
    public boolean isValidFileSize(MultipartFile file) {
        if (file == null) {
            return false;
        }
        return file.getSize() <= fileUploadProperties.getMaxSize();
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        if (!isValidFileType(file)) {
            throw new BusinessException("不支持的文件类型，仅支持: " + fileUploadProperties.getAllowedTypes());
        }

        if (!isValidFileSize(file)) {
            throw new BusinessException("文件大小超过限制: " + (fileUploadProperties.getMaxSize() / 1024 / 1024) + "MB");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid;
    }
}