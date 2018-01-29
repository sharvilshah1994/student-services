package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.model.Student;
import com.studentapp.studentapp.model.Subject;
import com.studentapp.studentapp.model.Teacher;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<ArrayNode> getSchoolData() {
        List<ObjectNode> resultSchools = new ArrayList<>();
        List<School> schoolList = schoolRepository.findAll();
        for (School school : schoolList) {
            int studentCount = 0;
            int teacherCount = 0;
            int subjectCount = 0;
            ObjectNode objectNode = objectMapper.createObjectNode();
            long schoolId = school.getId();
            Set<Student> studentSet = school.getStudents();
            for (Student student : studentSet) {
                if (student.getSchool().getId() == schoolId) {
                    studentCount++;
                }
            }
            Set<Teacher> teacherSet = school.getTeachers();
            for (Teacher teacher : teacherSet) {
                if (teacher.getSchool().getId() == schoolId) {
                    teacherCount++;
                }
            }
            Set<Subject> subjectSet = school.getSubjects();
            for (Subject subject : subjectSet) {
                if (subject.getSchool().getId() == schoolId) {
                    subjectCount++;
                }
            }
            objectNode.put(ID, schoolId);
            objectNode.put(SCHOOL_NAME, school.getSchoolName());
            objectNode.put(STREET_ADDRESS, school.getStreetAddress());
            objectNode.put(CITY, school.getCity());
            objectNode.put(STATE, school.getState());
            objectNode.put(COUNTRY, school.getCountry());
            objectNode.put(ZIP, school.getZip());
            objectNode.put(PHONE_NUMBER, school.getPhoneNumber());
            objectNode.put("teacher_count", teacherCount);
            objectNode.put("subject_count", subjectCount);
            objectNode.put("student_count", studentCount);
            resultSchools.add(objectNode);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectMapper.valueToTree(resultSchools));
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
        objectNode.put(ID, newSchool.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> updateSchoolData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(ID)) {
            objectNode.put(STATUS, "ERROR! `id` field is required to update school data.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        School school = schoolRepository.findOne(jsonNode.get(ID).asLong());
        if (school == null) {
            objectNode.put(STATUS, "ERROR! School with id: " + jsonNode.get(ID).asLong() + " does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (jsonNode.has(SCHOOL_NAME)) {
            school.setSchoolName(jsonNode.get(SCHOOL_NAME).asText());
        }
        if (jsonNode.has(STREET_ADDRESS)) {
            school.setStreetAddress(jsonNode.get(STREET_ADDRESS).asText());
        }
        if (jsonNode.has(CITY)) {
            school.setCity(jsonNode.get(CITY).asText());
        }
        if (jsonNode.has(STATE)) {
            school.setState(jsonNode.get(STATE).asText());
        }
        if (jsonNode.has(COUNTRY)) {
            school.setCountry(jsonNode.get(COUNTRY).asText());
        }
        if (jsonNode.has(ZIP)) {
            school.setZip(jsonNode.get(ZIP).asInt());
        }
        if (jsonNode.has(PHONE_NUMBER)) {
            school.setPhoneNumber(jsonNode.get(PHONE_NUMBER).asText());
        }
        schoolRepository.save(school);
        objectNode.put(STATUS, "SUCCESS! Modified the records.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> deleteSchoolData(long id) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        School school = schoolRepository.findOne(id);
        if (school == null) {
            objectNode.put(STATUS, "School with id: " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        schoolRepository.delete(id);
        objectNode.put(STATUS, "School with id: " + id + " is DELETED.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
