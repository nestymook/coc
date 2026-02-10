package com.example.formmanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.entity.Comment;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.service.FormService;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {
    
    @Autowired
    private FormService formService;
    
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> createForm(@RequestBody Form form, @RequestParam Long userId) {
        Form createdForm = formService.createForm(form, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", createdForm);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> createDraft(@RequestBody Form form, @RequestParam Long userId) {
        Form createdForm = formService.createForm(form, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", createdForm);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@PathVariable Long id, @RequestBody Form form) {
        Form updatedForm = formService.saveDraft(id, form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", updatedForm);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitForm(@PathVariable Long id, @RequestBody Form form) {
        Form updatedForm = formService.submitForm(id, form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", updatedForm);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/resubmit")
    public ResponseEntity<Map<String, Object>> resubmitForm(@PathVariable Long id, @RequestBody Form form) {
        Form updatedForm = formService.resubmitForm(id, form);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", updatedForm);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> returnForm(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Form updatedForm = formService.returnForm(id, reason);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", updatedForm);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/recommend")
    public ResponseEntity<Map<String, Object>> recommendForm(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Form updatedForm = formService.recommendForm(id, reason);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("form", updatedForm);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<Form>> getMyForms(@RequestParam(required = false) String status, @RequestParam Long userId) {
        List<Form> forms = formService.getMyForms(status, userId);
        return ResponseEntity.ok(forms);
    }
    
    @GetMapping("/action")
    public ResponseEntity<List<Form>> getActionsList(@RequestParam String sortBy) {
        List<Form> forms = formService.getActionsList(sortBy);
        return ResponseEntity.ok(forms);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable Long id) {
        Form form = formService.getFormById(id);
        return ResponseEntity.ok(form);
    }
    
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<Attachment>> getAttachments(@PathVariable Long id) {
        List<Attachment> attachments = formService.getAttachmentsByFormId(id);
        return ResponseEntity.ok(attachments);
    }
    
    @PostMapping("/{id}/attachments")
    public ResponseEntity<Map<String, Object>> uploadAttachment(@PathVariable Long id, @RequestBody Attachment attachment) {
        Attachment savedAttachment = formService.uploadAttachment(id, attachment);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("attachment", savedAttachment);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        List<Comment> comments = formService.getCommentsByFormId(id);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment savedComment = formService.addComment(id, comment);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("comment", savedComment);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteForm(@PathVariable Long id) {
        boolean deleted = formService.deleteForm(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        return ResponseEntity.ok(response);
    }
}
