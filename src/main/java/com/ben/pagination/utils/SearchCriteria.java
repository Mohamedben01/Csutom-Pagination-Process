package com.ben.pagination.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SearchCriteria {
    @JsonIgnore
    boolean isEmpty();
}
