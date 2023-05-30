package com.ben.pagination.service;

import com.ben.pagination.criteria.StudentCriteria;
import com.ben.pagination.entity.Student;
import com.ben.pagination.repository.StudentRepository;
import com.ben.pagination.repository.StudentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentRepositoryImpl studentRepositoryImpl;

    public Student saveStudent(Student student){
        return studentRepository.save(student);
    }

    public List<Student> allStudents(){
        return (List<Student>) studentRepository.findAll();
    }

    public Page<Student> search(StudentCriteria criteria, Integer currentPage, Integer pageSize, String sortDirection, String sortedField) {
        return studentRepositoryImpl.search(criteria, currentPage, pageSize, sortDirection, sortedField);
    }
}
