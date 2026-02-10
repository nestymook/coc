package com.example.formmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.formmanagement.entity.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByStatus(Form.FormStatus status);
    List<Form> findByStatusAndCreatorDepartment(Form.FormStatus status, String department);
    List<Form> findByStatusAndCreatorId(Form.FormStatus status, Long userId);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator WHERE f.id IN :ids")
    List<Form> findByIdsWithCreator(List<Long> ids);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator WHERE f.id = :id")
    Form findByIdWithCreator(Long id);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers WHERE f.id = :id")
    Form findByIdWithFamilyMembers(Long id);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers LEFT JOIN FETCH f.attachments WHERE f.id = :id")
    Form findByIdWithAttachments(Long id);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers LEFT JOIN FETCH f.attachments LEFT JOIN FETCH f.comments WHERE f.id = :id")
    Form findByIdWithDetails(Long id);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithCreator(Form.FormStatus status, Long userId);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithFamilyMembers(Form.FormStatus status, Long userId);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.attachments WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithAttachments(Form.FormStatus status, Long userId);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers LEFT JOIN FETCH f.attachments LEFT JOIN FETCH f.comments WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithDetails(Form.FormStatus status, Long userId);
    
    @Query("SELECT DISTINCT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.familyMembers LEFT JOIN FETCH f.attachments LEFT JOIN FETCH f.comments WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithDetailsDistinct(Form.FormStatus status, Long userId);
    
    @Query("SELECT f FROM Form f LEFT JOIN FETCH f.creator LEFT JOIN FETCH f.ra LEFT JOIN FETCH f.aa WHERE f.status = :status AND f.creator.id = :userId")
    List<Form> findByStatusAndCreatorIdWithDetailsNoForms(Form.FormStatus status, Long userId);
}
