package com.ben.pagination.utils;

import org.springframework.data.domain.Page;

import java.util.List;

public interface PagingRepositoryCustom <T, U extends SearchCriteria>{

    Long count(U crit);

    Page<T> search(U crit, Integer currentPage, Integer pageSize, String sortDirection, String SortedField);

    List<T> search(U crit, String SortDirection, String SortedField);
}
