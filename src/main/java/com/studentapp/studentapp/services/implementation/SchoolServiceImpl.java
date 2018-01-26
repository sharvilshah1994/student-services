package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchoolServiceImpl implements SchoolService {

    private static SchoolRepository schoolRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<School> getAllSchoolData() {
        return schoolRepository.findAll();
    }
}
