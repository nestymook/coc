package com.example.formmanagement.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.formmanagement.config.FileUploadConfig;
import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.service.FormService;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    @Autowired
    private FormService formService;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    @PostMapping("/attachment")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("formId") Long formId,
            @RequestParam(value = "filename", required = false) String filename) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check file size (10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File size exceeds 10MB limit");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Get upload directory
            File uploadDir = fileUploadConfig.getUploadDirectory();
            
            // Determine filename
            String originalFilename = file.getOriginalFilename();
            String safeFilename = filename != null ? filename : 
                (originalFilename != null ? originalFilename : "unknown");
            
            // Generate unique filename to avoid conflicts
            String uniqueFilename = System.currentTimeMillis() + "_" + safeFilename;
            Path filePath = Paths.get(uploadDir.getAbsolutePath(), uniqueFilename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create attachment entity
            Attachment attachment = new Attachment();
            attachment.setFilename(safeFilename);
            attachment.setPath("/uploads/" + uniqueFilename);
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            
            // Save attachment to database
            attachment = formService.uploadAttachment(formId, attachment);
            
            response.put("success", true);
            response.put("attachment", attachment);
            response.put("message", "File uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/attachment/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            Attachment attachment = formService.getAttachmentById(attachmentId);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Read file - extract the unique filename from the path
            String uniqueFilename = attachment.getPath().substring("/uploads/".length());
            Path filePath = Paths.get(fileUploadConfig.getUploadDirectory().getAbsolutePath(), uniqueFilename);
            byte[] fileContent = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + attachment.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}