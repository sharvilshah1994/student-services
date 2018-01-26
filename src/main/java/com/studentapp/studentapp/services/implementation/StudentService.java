package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.studentapp.studentapp.model.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {

    List<Student> getAllStudentData();
    ResponseEntity<?> addStudentData(JsonNode jsonNode);
    ResponseEntity<?> updateStudentData(JsonNode jsonNode);
    ResponseEntity<?> deleteStudentData(long id);
}
