package com.ben.pagination.repository;

import com.ben.pagination.entity.Student;
import com.ben.pagination.utils.PagingRepositoryCustom;
import com.ben.pagination.utils.SearchCriteria;

public interface StudentRepository extends PagingAndSortingRepository<Student, Long>, PagingRepositoryCustom<Student, SearchCriteria> {
}
