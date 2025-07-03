package cn.xlhealth.backend.service;

import cn.xlhealth.backend.config.properties.FileUploadProperties;
import cn.xlhealth.backend.ui.advice.BusinessException;
import cn.xlhealth.backend.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/**
 * 文件存储服务测试类
 * 
 * @author XLHealth Team
 * @version 1.0
 * @since 2024-12-19
 */
@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private FileUploadProperties fileUploadProperties;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // 配置文件上传属性
        lenient().when(fileUploadProperties.getPath()).thenReturn(tempDir.toString());
        lenient().when(fileUploadProperties.getUrlPrefix()).thenReturn("/uploads");
        lenient().when(fileUploadProperties.getAvatarDir()).thenReturn("avatars");
        lenient().when(fileUploadProperties.getAllowedTypes()).thenReturn("jpg,jpeg,png,gif");
        lenient().when(fileUploadProperties.getMaxSize()).thenReturn(5 * 1024 * 1024L); // 5MB
    }

    @Test
    void testStoreAvatar_Success() throws IOException {
        // 准备测试数据
        Long userId = 1L;
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                content);

        // 执行测试
        String result = fileStorageService.storeAvatar(file, userId);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/avatars/" + userId));
        assertTrue(result.endsWith(".jpg"));

        // 验证文件是否真的被保存
        String relativePath = result.replace("/uploads/", "");
        Path savedFile = tempDir.resolve(relativePath);
        assertTrue(Files.exists(savedFile));
        assertArrayEquals(content, Files.readAllBytes(savedFile));
    }

    @Test
    void testStoreFile_Success() throws IOException {
        // 准备测试数据
        String directory = "test";
        byte[] content = "test file content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content);

        // 执行测试
        String result = fileStorageService.storeFile(file, directory);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/" + directory));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void testValidateFile_InvalidType() {
        // 准备测试数据 - 不支持的文件类型
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "test content".getBytes());

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> fileStorageService.storeFile(file, "test"));

        assertTrue(exception.getMessage().contains("不支持的文件类型"));
    }

    @Test
    void testValidateFile_FileTooLarge() {
        // 准备测试数据 - 文件过大
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB，超过5MB限制
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> fileStorageService.storeFile(file, "test"));

        assertTrue(exception.getMessage().contains("文件大小超过限制"));
    }

    @Test
    void testValidateFile_EmptyFile() {
        // 准备测试数据 - 空文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> fileStorageService.storeFile(file, "test"));

        assertTrue(exception.getMessage().contains("文件不能为空"));
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // 先创建一个文件
        String directory = "test";
        byte[] content = "test file content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content);

        String fileUrl = fileStorageService.storeFile(file, directory);

        // 验证文件存在
        String relativePath = fileUrl.replace("/uploads/", "");
        Path savedFile = tempDir.resolve(relativePath);
        assertTrue(Files.exists(savedFile));

        // 删除文件
        boolean deleted = fileStorageService.deleteFile(fileUrl);

        // 验证删除结果
        assertTrue(deleted);
        assertFalse(Files.exists(savedFile));
    }

    @Test
    void testDeleteFile_FileNotExists() {
        // 尝试删除不存在的文件
        boolean deleted = fileStorageService.deleteFile("/uploads/nonexistent/file.txt");

        // 验证结果
        assertFalse(deleted);
    }

    @Test
    void testIsValidFileType_ValidTypes() {
        MockMultipartFile jpgFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile jpegFile = new MockMultipartFile("file", "test.jpeg", "image/jpeg", "content".getBytes());
        MockMultipartFile pngFile = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        MockMultipartFile gifFile = new MockMultipartFile("file", "test.gif", "image/gif", "content".getBytes());
        MockMultipartFile upperCaseFile = new MockMultipartFile("file", "TEST.JPG", "image/jpeg", "content".getBytes());

        assertTrue(fileStorageService.isValidFileType(jpgFile));
        assertTrue(fileStorageService.isValidFileType(jpegFile));
        assertTrue(fileStorageService.isValidFileType(pngFile));
        assertTrue(fileStorageService.isValidFileType(gifFile));
        assertTrue(fileStorageService.isValidFileType(upperCaseFile)); // 大小写不敏感
    }

    @Test
    void testIsValidFileType_InvalidTypes() {
        MockMultipartFile exeFile = new MockMultipartFile("file", "test.exe", "application/octet-stream",
                "content".getBytes());
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        MockMultipartFile docFile = new MockMultipartFile("file", "test.doc", "application/msword",
                "content".getBytes());
        MockMultipartFile noExtFile = new MockMultipartFile("file", "test", "text/plain", "content".getBytes());

        assertFalse(fileStorageService.isValidFileType(exeFile));
        assertFalse(fileStorageService.isValidFileType(pdfFile));
        assertFalse(fileStorageService.isValidFileType(docFile));
        assertFalse(fileStorageService.isValidFileType(noExtFile));
    }

    @Test
    void testIsValidFileSize_ValidSizes() {
        MockMultipartFile smallFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024] // 1KB
        );
        MockMultipartFile mediumFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024 * 1024] // 1MB
        );

        assertTrue(fileStorageService.isValidFileSize(smallFile));
        assertTrue(fileStorageService.isValidFileSize(mediumFile));
    }

    @Test
    void testIsValidFileSize_InvalidSize() {
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[6 * 1024 * 1024] // 6MB
        );

        assertFalse(fileStorageService.isValidFileSize(largeFile));
    }
}