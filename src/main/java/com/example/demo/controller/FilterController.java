package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RequestDto;
import com.example.demo.entity.Student;
import com.example.demo.repo.StudentRepository;
import com.example.demo.service.FilterSpecification;

import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FilterSpecification<Student> studentFilterSpecification;

   @PostMapping("/specification")
    public Page<Student> getStudents(@RequestBody RequestDto requestDto,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNo,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Specification<Student> searchSpecification = studentFilterSpecification
                .getSearchSpecificationList(requestDto.getSearchRequestDto());
        return studentRepository.findAll(searchSpecification, pageable);
    }



}
