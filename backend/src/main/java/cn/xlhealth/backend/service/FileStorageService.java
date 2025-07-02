package cn.xlhealth.backend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 存储文件
     * 
     * @param file 上传的文件
     * @param directory 存储目录
     * @return 文件访问URL
     */
    String storeFile(MultipartFile file, String directory);
    
    /**
     * 存储用户头像
     * 
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像访问URL
     */
    String storeAvatar(MultipartFile file, Long userId);
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 验证文件类型
     * 
     * @param file 文件
     * @return 是否为允许的类型
     */
    boolean isValidFileType(MultipartFile file);
    
    /**
     * 验证文件大小
     * 
     * @param file 文件
     * @return 是否在允许的大小范围内
     */
    boolean isValidFileSize(MultipartFile file);
}