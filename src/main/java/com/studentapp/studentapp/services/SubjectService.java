package com.studentapp.studentapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.Subject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjectData();

    ResponseEntity<ObjectNode> addSubjectData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> updateSubjectData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> deleteSubjectData(long id);
}
