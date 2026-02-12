package com.example.formmanagement.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.formmanagement.config.FileUploadConfig;
import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.service.FormService;

public class FileUploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FormService formService;

    @Mock
    private FileUploadConfig fileUploadConfig;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileUploadController).build();
    }

    @Test
    public void testUploadAttachment_Success() throws Exception {
        // Arrange
        Form form = new Form();
        form.setId(1L);
        form.setTitle("Test Form");

        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFilename("test.txt");
        attachment.setPath("/uploads/1234567890_test.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100L);

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Test file content".getBytes()
        );

        when(formService.uploadAttachment(anyLong(), any(Attachment.class))).thenReturn(attachment);
        when(fileUploadConfig.getUploadDirectory()).thenReturn(Files.createTempDirectory("test-upload").toFile());

        // Act & Assert
        mockMvc.perform(multipart("/api/upload/attachment")
                .file(mockFile)
                .param("formId", "1")
                .param("filename", "test.txt")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.attachment.filename").value("test.txt"));

        verify(formService, times(1)).uploadAttachment(eq(1L), any(Attachment.class));
    }

    @Test
    public void testUploadAttachment_EmptyFile() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "",
            "text/plain",
            new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/upload/attachment")
                .file(mockFile)
                .param("formId", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("File is empty"));

        verify(formService, never()).uploadAttachment(anyLong(), any(Attachment.class));
    }

    @Test
    public void testUploadAttachment_FileSizeExceedsLimit() throws Exception {
        // Arrange
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "large.txt",
            "text/plain",
            largeContent
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/upload/attachment")
                .file(mockFile)
                .param("formId", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("File size exceeds 10MB limit"));

        verify(formService, never()).uploadAttachment(anyLong(), any(Attachment.class));
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

        when(formService.getAttachmentById(1L)).thenReturn(attachment);

        // Create a temporary directory and file matching the expected path
        File tempDir = Files.createTempDirectory("test-upload").toFile();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "1234567890_test.txt");
        Files.write(tempFile, "Test file content".getBytes());

        when(fileUploadConfig.getUploadDirectory()).thenReturn(tempDir);

        // Act
        MvcResult result = mockMvc.perform(get("/api/upload/attachment/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
            .andReturn();

        // Assert
        byte[] responseContent = result.getResponse().getContentAsByteArray();
        assertArrayEquals("Test file content".getBytes(), responseContent);

        // Cleanup
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir.toPath());
    }

    @Test
    public void testDownloadAttachment_NotFound() throws Exception {
        // Arrange
        when(formService.getAttachmentById(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/upload/attachment/{id}", 999L))
            .andExpect(status().isNotFound());

        verify(formService, times(1)).getAttachmentById(999L);
    }

    @Test
    public void testDownloadAttachment_IOException() throws Exception {
        // Arrange
        when(formService.getAttachmentById(anyLong())).thenReturn(new Attachment());
        when(fileUploadConfig.getUploadDirectory()).thenThrow(new RuntimeException("IO Error"));

        // Act & Assert
        mockMvc.perform(get("/api/upload/attachment/{id}", 1L))
            .andExpect(status().isInternalServerError());

        verify(formService, times(1)).getAttachmentById(1L);
    }

    @Test
    public void testUploadAttachment_WithFormIdOnly() throws Exception {
        // Arrange
        Form form = new Form();
        form.setId(1L);

        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFilename("original.txt");
        attachment.setPath("/uploads/1234567890_original.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(50L);

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "original.txt",
            "text/plain",
            "Content".getBytes()
        );

        when(formService.uploadAttachment(anyLong(), any(Attachment.class))).thenReturn(attachment);
        when(fileUploadConfig.getUploadDirectory()).thenReturn(Files.createTempDirectory("test-upload").toFile());

        // Act & Assert
        mockMvc.perform(multipart("/api/upload/attachment")
                .file(mockFile)
                .param("formId", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.attachment.filename").value("original.txt"));
    }
}