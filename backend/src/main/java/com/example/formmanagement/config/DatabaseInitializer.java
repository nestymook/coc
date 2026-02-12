package com.example.formmanagement.config;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.formmanagement.entity.Attachment;
import com.example.formmanagement.entity.Comment;
import com.example.formmanagement.entity.FamilyMember;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.entity.User;
import com.example.formmanagement.repository.AttachmentRepository;
import com.example.formmanagement.repository.CommentRepository;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.repository.UserRepository;

@Configuration
public class DatabaseInitializer {
    
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, AttachmentRepository attachmentRepository,
            CommentRepository commentRepository, FormRepository formRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Clear existing data
            formRepository.deleteAll();
            commentRepository.deleteAll();
            attachmentRepository.deleteAll();
            userRepository.deleteAll();
            
            // Create RA user
            User ra = new User();
            ra.setUsername("ra_user");
            ra.setPassword(passwordEncoder.encode("password123"));
            ra.setRole(User.Role.RA);
            ra.setPosition("RA");
            ra.setDepartment("HR");
            ra.setEmail("ra@example.com");
            ra.setPhone("1234567890");
            ra.setCanViewRecords(true);
            ra = userRepository.save(ra);
            
            // Create AA user
            User aa = new User();
            aa.setUsername("aa_user");
            aa.setPassword(passwordEncoder.encode("password123"));
            aa.setRole(User.Role.AA);
            aa.setPosition("AA");
            aa.setDepartment("Finance");
            aa.setEmail("aa@example.com");
            aa.setPhone("1234567891");
            aa.setCanViewRecords(true);
            aa = userRepository.save(aa);
            
            // Create RS user
            User rs = new User();
            rs.setUsername("rs_user");
            rs.setPassword(passwordEncoder.encode("password123"));
            rs.setRole(User.Role.RS);
            rs.setPosition("Staff");
            rs.setDepartment("HR");
            rs.setEmail("rs@example.com");
            rs.setPhone("1234567892");
            rs.setCanViewRecords(false);
            rs = userRepository.save(rs);
            
            // 1. DRAFT form (created by RS)
            Form draftForm = new Form();
            draftForm.setCode("FORM-2026-001");
            draftForm.setTitle("Leave Application - Draft");
            draftForm.setStatus(Form.FormStatus.DRAFT);
            draftForm.setSubmissionDate(new Date());
            draftForm.setLastActionDate(new Date());
            draftForm.setFormType("Leave");
            draftForm.setOtherInfo("Annual leave request");
            draftForm.setDeclarationChecked(true);
            draftForm.setCreator(rs);
            draftForm.setCreatorDepartment(rs.getDepartment());
            draftForm.setRa(ra);
            draftForm.setAa(aa);
            draftForm.setRecommended(false);
            draftForm = formRepository.save(draftForm);
            
            // Add family members to draft form
            FamilyMember fm1 = new FamilyMember();
            fm1.setForm(draftForm);
            fm1.setName("John Smith");
            fm1.setRelationship("Spouse");
            fm1.setGender("Male");
            fm1.setIdNumber("Z123567(8)");
            FamilyMember fm2 = new FamilyMember();
            fm2.setForm(draftForm);
            fm2.setName("Jane Smith");
            fm2.setRelationship("Child");
            fm2.setGender("Female");
            fm2.setIdNumber("G123567(A)");
            Set<FamilyMember> draftFamilyMembers = new HashSet<>();
            draftFamilyMembers.add(fm1);
            draftFamilyMembers.add(fm2);
            draftForm.setFamilyMembers(draftFamilyMembers);
            formRepository.save(draftForm);
            
            // 2. PROCESSING form (submitted by RS, pending RA review)
            Form processingForm = new Form();
            processingForm.setCode("FORM-2026-002");
            processingForm.setTitle("Business Trip Application");
            processingForm.setStatus(Form.FormStatus.PROCESSING);
            processingForm.setSubmissionDate(new Date());
            processingForm.setLastActionDate(new Date());
            processingForm.setFormType("Business Trip");
            processingForm.setOtherInfo("Trip to Beijing for conference");
            processingForm.setDeclarationChecked(true);
            processingForm.setCreator(rs);
            processingForm.setCreatorDepartment(rs.getDepartment());
            processingForm.setRa(ra);
            processingForm.setAa(aa);
            processingForm.setRecommended(false);
            processingForm = formRepository.save(processingForm);
            
            // Add family members to processing form
            FamilyMember fm3 = new FamilyMember();
            fm3.setForm(processingForm);
            fm3.setName("Bob Smith");
            fm3.setRelationship("Spouse");
            fm3.setGender("Male");
            fm3.setIdNumber("H123567(2)");
            FamilyMember fm4 = new FamilyMember();
            fm4.setForm(processingForm);
            fm4.setName("Alice Smith");
            fm4.setRelationship("Child");
            fm4.setGender("Female");
            fm4.setIdNumber("A123567(3)");
            Set<FamilyMember> processingFamilyMembers = new HashSet<>();
            processingFamilyMembers.add(fm3);
            processingFamilyMembers.add(fm4);
            processingForm.setFamilyMembers(processingFamilyMembers);
            formRepository.save(processingForm);
            
            // 3. RETURNED form (returned by RA for revision)
            Form returnedForm = new Form();
            returnedForm.setCode("FORM-2026-003");
            returnedForm.setTitle("Purchase Application - Returned");
            returnedForm.setStatus(Form.FormStatus.RETURNED);
            returnedForm.setSubmissionDate(new Date());
            returnedForm.setLastActionDate(new Date());
            returnedForm.setFormType("Purchase");
            returnedForm.setOtherInfo("Office supplies purchase");
            returnedForm.setDeclarationChecked(true);
            returnedForm.setCreator(rs);
            returnedForm.setCreatorDepartment(rs.getDepartment());
            returnedForm.setRa(ra);
            returnedForm.setAa(aa);
            returnedForm.setReturnReason("Missing required documents - please attach proof of quotation");
            returnedForm.setRecommended(false);
            returnedForm = formRepository.save(returnedForm);
            
            // Add family members to returned form
            FamilyMember fm5 = new FamilyMember();
            fm5.setForm(returnedForm);
            fm5.setName("Charlie Smith");
            fm5.setRelationship("Spouse");
            fm5.setGender("Male");
            fm5.setIdNumber("A123567(3)");
            FamilyMember fm6 = new FamilyMember();
            fm6.setForm(returnedForm);
            fm6.setName("Diana Smith");
            fm6.setRelationship("Child");
            fm6.setGender("Female");
            fm6.setIdNumber("A123567(3)");
            Set<FamilyMember> returnedFamilyMembers = new HashSet<>();
            returnedFamilyMembers.add(fm5);
            returnedFamilyMembers.add(fm6);
            returnedForm.setFamilyMembers(returnedFamilyMembers);
            formRepository.save(returnedForm);
            
            // Add comment to returned form
            Comment comment = new Comment();
            comment.setForm(returnedForm);
            comment.setCreatedBy(ra);
            comment.setCreatedDate(new Date());
            comment.setContent("Please provide the quotation documents for the purchase items.");
            comment.setDescription("Missing documents");
            comment.setType(Comment.CommentType.RETURNED);
            comment = commentRepository.save(comment);
            Set<Comment> returnedComments = new HashSet<>();
            returnedComments.add(comment);
            returnedForm.setComments(returnedComments);
            formRepository.save(returnedForm);
            
            // 4. COMPLETED form (approved and completed)
            Form completedForm = new Form();
            completedForm.setCode("FORM-2026-004");
            completedForm.setTitle("Training Application - Completed");
            completedForm.setStatus(Form.FormStatus.COMPLETED);
            completedForm.setSubmissionDate(new Date());
            completedForm.setLastActionDate(new Date());
            completedForm.setFormType("Training");
            completedForm.setOtherInfo("Leadership training program");
            completedForm.setDeclarationChecked(true);
            completedForm.setCreator(rs);
            completedForm.setCreatorDepartment(rs.getDepartment());
            completedForm.setRa(ra);
            completedForm.setAa(aa);
            completedForm.setRecommended(true);
            completedForm = formRepository.save(completedForm);
            
            // Add family members to completed form
            FamilyMember fm7 = new FamilyMember();
            fm7.setForm(completedForm);
            fm7.setName("Eve Smith");
            fm7.setRelationship("Spouse");
            fm7.setGender("Female");
            fm7.setIdNumber("U123567(3)");            FamilyMember fm8 = new FamilyMember();
            fm8.setForm(completedForm);
            fm8.setName("Frank Smith");
            fm8.setRelationship("Child");
            fm8.setIdNumber("R123567(3)");
            Set<FamilyMember> completedFamilyMembers = new HashSet<>();
            completedFamilyMembers.add(fm7);
            completedFamilyMembers.add(fm8);
            completedForm.setFamilyMembers(completedFamilyMembers);
            formRepository.save(completedForm);
            
            // Add attachment to completed form
            Attachment attachment = new Attachment();
            attachment.setForm(completedForm);
            attachment.setFilename("training_certificate.pdf");
            attachment.setSize(1024L);
            attachment.setPath("/uploads/training_certificate.pdf");
            attachment.setContentType("application/pdf");
            attachment = attachmentRepository.save(attachment);
            Set<Attachment> completedAttachments = new HashSet<>();
            completedAttachments.add(attachment);
            completedForm.setAttachments(completedAttachments);
            formRepository.save(completedForm);
            
            System.out.println("Sample data initialized successfully!");
            System.out.println("RA user ID: " + ra.getId());
            System.out.println("AA user ID: " + aa.getId());
            System.out.println("RS user ID: " + rs.getId());
            System.out.println("Total forms: " + formRepository.count());
        };
    }
}