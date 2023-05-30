package com.ben.pagination.controller;

import com.ben.pagination.criteria.StudentCriteria;
import com.ben.pagination.entity.Student;
import com.ben.pagination.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/save")
    public ResponseEntity<Student> saveStudent(@RequestBody Student student){
        return ResponseEntity.ok(studentService.saveStudent(student));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> students(){
        return ResponseEntity.ok(studentService.allStudents());
    }

    @PostMapping("/searchWithPagination")
    public Page<Student> findStudentByCriteria( @RequestParam(required = false, defaultValue = "0") final Integer currentPage,
                                                  @RequestParam(required = false, defaultValue = "5") final Integer pageSize,
                                                  @RequestParam(required = false) final String sortDirection,
                                                  @RequestParam(required = false) final String sortedField,
                                                  @RequestBody final StudentCriteria criteria) {
        return studentService.search(criteria, currentPage, pageSize, sortDirection, sortedField);
    }
}
