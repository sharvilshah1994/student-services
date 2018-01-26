package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.studentapp.studentapp.config.Constants.*;

@Component
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
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

    @Override
    public ResponseEntity<ObjectNode> addSchoolData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(SCHOOL_NAME)) {
            objectNode.put(STATUS, "ERROR! Cannot add data without `school_name` parameter");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String schoolName = jsonNode.get(SCHOOL_NAME).asText();
        School newSchool = new School();
        newSchool.setSchoolName(schoolName);
        if (jsonNode.has(STREET_ADDRESS)) {
            newSchool.setStreetAddress(jsonNode.get(STREET_ADDRESS).asText());
        }
        if (jsonNode.has(CITY)) {
            newSchool.setCity(jsonNode.get(CITY).asText());
        }
        if (jsonNode.has(STATE)) {
            newSchool.setState(jsonNode.get(STATE).asText());
        }
        if (jsonNode.has(ZIP)) {
            newSchool.setZip(jsonNode.get(ZIP).asInt());
        }
        if (jsonNode.has(COUNTRY)) {
            newSchool.setCountry(jsonNode.get(COUNTRY).asText());
        }
        if (jsonNode.has(PHONE_NUMBER)) {
            newSchool.setPhoneNumber(jsonNode.get(PHONE_NUMBER).asText());
        }
        schoolRepository.save(newSchool);
        objectNode.put("id", newSchool.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
