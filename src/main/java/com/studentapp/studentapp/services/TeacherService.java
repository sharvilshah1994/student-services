package com.studentapp.studentapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.Teacher;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TeacherService {

    List<Teacher> getAllTeacherData();

    ResponseEntity<ObjectNode> addTeacherData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> updateTeacherData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> deleteTeacherData(long id);
}
