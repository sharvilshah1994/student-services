package com.studentapp.studentapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SchoolService {

    ResponseEntity<ArrayNode> getSchoolData();

    ResponseEntity<ObjectNode> addSchoolData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> updateSchoolData(JsonNode jsonNode);

    ResponseEntity<ObjectNode> deleteSchoolData(long id);
}
