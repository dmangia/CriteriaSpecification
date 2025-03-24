package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class SearchRequestDto {
    private String column;
    private String value;
    private Operation operation;
    private String joinTable;
    private TypeCase typeCase;
    private Boolean orCase;

    public enum Operation {
        EQUAL, LIKE, IN, GREATER_THAN, GREATER_EQ_THAN,LESS_THAN,
        LESS_EQ_THAN,BETWEEN, IS_NULL,IS_NOT_NULL,JOIN;
    }

    public enum TypeCase {  //si può omettere se non è DATE
        DATE, NUMBER;
    }

}
