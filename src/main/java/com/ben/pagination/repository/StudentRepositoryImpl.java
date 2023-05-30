package com.ben.pagination.repository;

import com.ben.pagination.criteria.StudentCriteria;
import com.ben.pagination.entity.Student;
import com.ben.pagination.utils.PagingRepositoryImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StudentRepositoryImpl extends PagingRepositoryImpl<Student, StudentCriteria> {
    @Override
    protected void setEntityClass() {
        this.entityClass = Student.class;
    }

    @Override
    protected Predicate getPredicate(StudentCriteria criteria, CriteriaBuilder cb, Root<Student> entityRoot) {
        Predicate pred = cb.conjunction();
        pred = helper.addSimpleLike(pred, cb, entityRoot, criteria, "fullName", StudentCriteria::getFullName);
        pred = helper.addSimpleLike(pred, cb, entityRoot, criteria, "email", StudentCriteria::getEmail);
        return pred;
    }
}
