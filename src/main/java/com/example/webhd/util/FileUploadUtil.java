package com.example.webhd.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploadUtil {

    /**
     * 上传图片
     * @param file 上传的文件
     * @param uploadPath 上传路径
     * @return 新文件名
     */
    public static String uploadImage(MultipartFile file, String uploadPath) throws IOException {
        // 1. 验证文件类型
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IllegalArgumentException("只支持 JPG, PNG, GIF, BMP 格式的图片");
        }

        // 2. 验证文件大小（2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过2MB");
        }

        // 3. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;

        // 4. 确保目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 5. 保存文件
        File destFile = new File(uploadPath + newFileName);
        file.transferTo(destFile);

        return newFileName;
    }

    /**
     * 验证图片类型
     */
    private static boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/bmp")
        );
    }
}