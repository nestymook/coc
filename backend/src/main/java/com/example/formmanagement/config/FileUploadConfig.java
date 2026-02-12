package com.example.formmanagement.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            String uploadDirPath = new File(uploadDir).getCanonicalPath();
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadDirPath + "/");
        } catch (IOException e) {
            throw new RuntimeException("Failed to configure file upload directory", e);
        }
    }
    
    public String getUploadDir() {
        return uploadDir;
    }
    
    public File getUploadDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
