package com.example.formmanagement.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.formmanagement.config.FileUploadConfig;
import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.service.FormService;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
public class AttachmentController {
    
    @Autowired
    private FormService formService;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = formService.getAttachmentsByFormId(id).stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // Read file from filesystem
            String uniqueFilename = attachment.getPath().substring("/uploads/".length());
            Path filePath = Paths.get(fileUploadConfig.getUploadDirectory().getAbsolutePath(), uniqueFilename);
            byte[] content = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
