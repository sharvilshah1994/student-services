package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.studentapp.studentapp.model.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {

    List<Student> getAllStudentData();
    ResponseEntity<Student> addStudentData(JsonNode jsonNode);
}
