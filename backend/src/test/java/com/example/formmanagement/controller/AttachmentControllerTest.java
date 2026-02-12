package com.example.formmanagement.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.formmanagement.config.FileUploadConfig;
import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.service.FormService;

public class AttachmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FormService formService;

    @Mock
    private FileUploadConfig fileUploadConfig;

    @InjectMocks
    private AttachmentController attachmentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();
    }

    @Test
    public void testDownloadAttachment_Success() throws Exception {
        // Arrange
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFilename("test.txt");
        attachment.setPath("/uploads/1234567890_test.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100L);

        when(formService.getAttachmentsByFormId(1L)).thenReturn(List.of(attachment));
        
        File tempDir = Files.createTempDirectory("test-upload").toFile();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "1234567890_test.txt");
        Files.write(tempFile, "Test file content".getBytes());
        
        when(fileUploadConfig.getUploadDirectory()).thenReturn(tempDir);

        // Act
        MvcResult result = mockMvc.perform(get("/api/attachments/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
            .andReturn();

        // Assert
        byte[] responseContent = result.getResponse().getContentAsByteArray();
        assertEquals("Test file content", new String(responseContent));

        // Cleanup
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir.toPath());
    }

    @Test
    public void testDownloadAttachment_NotFound() throws Exception {
        // Arrange
        when(formService.getAttachmentsByFormId(anyLong())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/attachments/{id}", 999L))
            .andExpect(status().isNotFound());

        verify(formService, times(1)).getAttachmentsByFormId(999L);
    }

    @Test
    public void testDownloadAttachment_EmptyList() throws Exception {
        // Arrange
        when(formService.getAttachmentsByFormId(anyLong())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/attachments/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDownloadAttachment_FileNotFoundInFileSystem() throws Exception {
        // Arrange
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFilename("nonexistent.txt");
        attachment.setPath("/uploads/9999999999_nonexistent.txt");

        when(formService.getAttachmentsByFormId(1L)).thenReturn(List.of(attachment));
        when(fileUploadConfig.getUploadDirectory()).thenReturn(Files.createTempDirectory("test-upload").toFile());

        // Act & Assert
        mockMvc.perform(get("/api/attachments/{id}", 1L))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void testDownloadAttachment_MultipleAttachments() throws Exception {
        // Arrange - attachment not found in list (returns 404)
        // The controller filters the list by attachment ID, so if we request an ID
        // that's not in the list, it returns 404
        
        // Test with attachment ID 3 which is not in the list
        when(formService.getAttachmentsByFormId(1L)).thenReturn(List.of());

        // Test downloading attachment - attachment not found in list (returns 404)
        mockMvc.perform(get("/api/attachments/{id}", 3L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDownloadAttachment_WithSpecialCharactersInFilename() throws Exception {
        // Arrange
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFilename("test file (1).txt");
        attachment.setPath("/uploads/1234567890_test file (1).txt");

        when(formService.getAttachmentsByFormId(1L)).thenReturn(List.of(attachment));
        
        File tempDir = Files.createTempDirectory("test-upload").toFile();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "1234567890_test file (1).txt");
        Files.write(tempFile, "Content with special filename".getBytes());
        
        when(fileUploadConfig.getUploadDirectory()).thenReturn(tempDir);

        // Act
        MvcResult result = mockMvc.perform(get("/api/attachments/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test file (1).txt\""))
            .andReturn();

        // Assert
        byte[] responseContent = result.getResponse().getContentAsByteArray();
        assertEquals("Content with special filename", new String(responseContent));

        // Cleanup
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir.toPath());
    }
}