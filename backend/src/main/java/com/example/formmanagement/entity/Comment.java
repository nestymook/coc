package com.example.formmanagement.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;
    
    private String content;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    private Date createdDate;
    private Date actionDate;
    
    @Enumerated(EnumType.STRING)
    private CommentType type;
    
    public enum CommentType {
        RETURNED, REVIEWED, REJECTED, INSTRUCTIONS
    }
}