package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.Student;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StudentService {

    List<Student> getAllStudentData();
    ResponseEntity<ObjectNode> addStudentData(JsonNode jsonNode);
    ResponseEntity<ObjectNode> updateStudentData(JsonNode jsonNode);
    ResponseEntity<ObjectNode> deleteStudentData(long id);
}
