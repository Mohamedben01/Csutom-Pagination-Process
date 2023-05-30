package com.ben.pagination.criteria;

import com.ben.pagination.utils.SearchCriteria;
import lombok.Data;

@Data
public class StudentCriteria implements SearchCriteria {
    private String fullName;
    private String email;

    @Override
    public boolean isEmpty() {
        return fullName == null && email == null;
    }
}
