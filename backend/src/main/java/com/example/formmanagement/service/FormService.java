package com.example.formmanagement.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.entity.Comment;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.repository.AttachmentRepository;
import com.example.formmanagement.repository.CommentRepository;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.repository.UserRepository;

@Service
public class FormService {
    
    @Autowired
    private FormRepository formRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Transactional
    public Form createForm(Form form, Long userId) {
        form.setCreator(userRepository.findById(userId).orElse(null));
        form.setSubmissionDate(new Date());
        form.setStatus(Form.FormStatus.DRAFT);
        
        // Set form reference on family members
        if (form.getFamilyMembers() != null) {
            form.getFamilyMembers().forEach(fm -> fm.setForm(form));
        }
        
        // Set form reference on attachments
        if (form.getAttachments() != null) {
            form.getAttachments().forEach(att -> att.setForm(form));
        }
        
        return formRepository.save(form);
    }
    
    @Transactional
    public Form updateForm(Form form) {
        form.setLastActionDate(new Date());
        return formRepository.save(form);
    }
    
    @Transactional
    public Form submitForm(Long formId, Form form) {
        Form existingForm = formRepository.findById(formId).orElse(null);
        if (existingForm != null) {
            existingForm.setTitle(form.getTitle());
            existingForm.setFormType(form.getFormType());
            existingForm.setOtherInfo(form.getOtherInfo());
            existingForm.setDeclarationChecked(form.isDeclarationChecked());
            existingForm.setCreatorDepartment(form.getCreatorDepartment());
            
            // Update family members - clear and add to avoid orphan removal issue
            existingForm.getFamilyMembers().clear();
            if (form.getFamilyMembers() != null) {
                form.getFamilyMembers().forEach(fm -> {
                    fm.setForm(existingForm);
                    existingForm.getFamilyMembers().add(fm);
                });
            }
            
            // Update attachments - clear and add to avoid orphan removal issue
            existingForm.getAttachments().clear();
            if (form.getAttachments() != null) {
                form.getAttachments().forEach(att -> {
                    att.setForm(existingForm);
                    existingForm.getAttachments().add(att);
                });
            }
            
            existingForm.setStatus(Form.FormStatus.PROCESSING);
            existingForm.setSubmissionDate(new Date());
            existingForm.setLastActionDate(new Date());
            return formRepository.save(existingForm);
        }
        return null;
    }
    
    @Transactional
    public Form saveDraft(Long formId, Form form) {
        // If formId is null, create a new form
        if (formId == null) {
            return createForm(form, form.getCreator() != null ? form.getCreator().getId() : null);
        }
        
        Form existingForm = formRepository.findById(formId).orElse(null);
        if (existingForm != null) {
            existingForm.setTitle(form.getTitle());
            existingForm.setFormType(form.getFormType());
            existingForm.setOtherInfo(form.getOtherInfo());
            existingForm.setDeclarationChecked(form.isDeclarationChecked());
            existingForm.setCreatorDepartment(form.getCreatorDepartment());
            
            // Update family members - clear and add to avoid orphan removal issue
            existingForm.getFamilyMembers().clear();
            if (form.getFamilyMembers() != null) {
                form.getFamilyMembers().forEach(fm -> {
                    fm.setForm(existingForm);
                    existingForm.getFamilyMembers().add(fm);
                });
            }
            
            // Update attachments - clear and add to avoid orphan removal issue
            existingForm.getAttachments().clear();
            if (form.getAttachments() != null) {
                form.getAttachments().forEach(att -> {
                    att.setForm(existingForm);
                    existingForm.getAttachments().add(att);
                });
            }
            
            existingForm.setStatus(Form.FormStatus.DRAFT);
            existingForm.setLastActionDate(new Date());
            return formRepository.save(existingForm);
        }
        return null;
    }
    
    @Transactional
    public Form resubmitForm(Long formId, Form form) {
        Form existingForm = formRepository.findById(formId).orElse(null);
        if (existingForm != null) {
            existingForm.setTitle(form.getTitle());
            existingForm.setFormType(form.getFormType());
            existingForm.setOtherInfo(form.getOtherInfo());
            existingForm.setDeclarationChecked(form.isDeclarationChecked());
            existingForm.setCreatorDepartment(form.getCreatorDepartment());
            
            // Update family members - clear and add to avoid orphan removal issue
            existingForm.getFamilyMembers().clear();
            if (form.getFamilyMembers() != null) {
                form.getFamilyMembers().forEach(fm -> {
                    fm.setForm(existingForm);
                    existingForm.getFamilyMembers().add(fm);
                });
            }
            
            // Update attachments - clear and add to avoid orphan removal issue
            existingForm.getAttachments().clear();
            if (form.getAttachments() != null) {
                form.getAttachments().forEach(att -> {
                    att.setForm(existingForm);
                    existingForm.getAttachments().add(att);
                });
            }
            
            existingForm.setStatus(Form.FormStatus.PROCESSING);
            existingForm.setLastActionDate(new Date());
            return formRepository.save(existingForm);
        }
        return null;
    }
    
    @Transactional
    public Form returnForm(Long formId, String reason) {
        Form existingForm = formRepository.findById(formId).orElse(null);
        if (existingForm != null) {
            existingForm.setStatus(Form.FormStatus.RETURNED);
            existingForm.setReturnReason(reason);
            existingForm.setLastActionDate(new Date());
            return formRepository.save(existingForm);
        }
        return null;
    }
    
    @Transactional
    public Form recommendForm(Long formId, String reason) {
        Form existingForm = formRepository.findById(formId).orElse(null);
        if (existingForm != null) {
            existingForm.setStatus(Form.FormStatus.COMPLETED);
            existingForm.setLastActionDate(new Date());
            return formRepository.save(existingForm);
        }
        return null;
    }
    
    public Form getFormById(Long id) {
        return formRepository.findByIdWithDetails(id);
    }
    
    @Transactional(readOnly = true)
    public List<Form> getMyForms(String status, Long userId) {
        Form.FormStatus formStatus = status != null && !status.isEmpty() ? 
            Form.FormStatus.valueOf(status) : Form.FormStatus.DRAFT;
        return formRepository.findByStatusAndCreatorIdWithDetailsDistinct(formStatus, userId);
    }
    
    public List<Form> getActionsList(String sortBy) {
        List<Form> forms = formRepository.findByStatus(Form.FormStatus.PROCESSING);
        return forms.stream()
                .sorted((f1, f2) -> {
                    switch (sortBy) {
                        case "DEPARTMENT":
                            return f1.getCreator().getDepartment().compareTo(f2.getCreator().getDepartment());
                        case "MONTH":
                            return f1.getSubmissionDate().compareTo(f2.getSubmissionDate());
                        case "FORM":
                            return f1.getTitle().compareTo(f2.getTitle());
                        default:
                            return 0;
                    }
                })
                .collect(Collectors.toList());
    }
    
    public List<Attachment> getAttachmentsByFormId(Long formId) {
        return attachmentRepository.findByFormId(formId);
    }
    
    public List<Comment> getCommentsByFormId(Long formId) {
        return commentRepository.findByFormId(formId);
    }
    
    @Transactional
    public Comment addComment(Long formId, Comment comment) {
        Form form = formRepository.findById(formId).orElse(null);
        if (form != null) {
            comment.setForm(form);
            comment.setActionDate(new Date());
            return commentRepository.save(comment);
        }
        return null;
    }
    
    @Transactional
    public Attachment uploadAttachment(Long formId, Attachment attachment) {
        Form form = formRepository.findById(formId).orElse(null);
        if (form != null) {
            attachment.setForm(form);
            Attachment savedAttachment = attachmentRepository.save(attachment);
            form.getAttachments().add(savedAttachment);
            return savedAttachment;
        }
        return null;
    }
    
    @Transactional
    public boolean deleteForm(Long formId) {
        if (formRepository.existsById(formId)) {
            formRepository.deleteById(formId);
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public Attachment getAttachmentById(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }
}
