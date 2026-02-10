package com.example.formmanagement.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String code;
    
    private String title;
    
    @Enumerated(EnumType.STRING)
    private FormStatus status;
    
    private Date submissionDate;
    private Date lastActionDate;
    private String formType;
    private String otherInfo;
    private boolean declarationChecked;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @Column(name = "creator_department", insertable = false, updatable = false)
    private String creatorDepartment;
    
    @ManyToOne
    @JoinColumn(name = "ra_id")
    private User ra;
    
    @ManyToOne
    @JoinColumn(name = "aa_id")
    private User aa;
    
    private boolean isRecommended;
    private String returnReason;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FamilyMember> familyMembers = new HashSet<>();
    
    @JsonManagedReference
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments = new HashSet<>();
    
    @JsonManagedReference
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();
    
    public enum FormStatus {
        DRAFT, PROCESSING, RETURNED, COMPLETED
    }
}