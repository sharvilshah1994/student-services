package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.model.Student;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.repository.StudentRepository;
import com.studentapp.studentapp.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.studentapp.studentapp.config.Constants.*;

@Component
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, SchoolRepository schoolRepository) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Student> getAllStudentData() {
        return studentRepository.findAll();
    }

    @Override
    public ResponseEntity<ObjectNode> addStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(FIRST_NAME) || !jsonNode.has(LAST_NAME)
                || !jsonNode.has(AGE) || !jsonNode.has(SCHOOL_ID)) {
            objectNode.put(STATUS, "ERROR! `first_name`, `last_name`, `age` & `school_id` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String firstName = jsonNode.get(FIRST_NAME).asText();
        String lastName = jsonNode.get(LAST_NAME).asText();
        int age = jsonNode.get(AGE).asInt();
        long schoolId = jsonNode.get(SCHOOL_ID).asLong();
        School school = schoolRepository.findOne(schoolId);
        if (school == null) {
            objectNode.put(STATUS, "ERROR! `school_id` " + schoolId + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (studentRepository.findByFirstNameAndLastNameAndAgeAndSchool(firstName, lastName, age, school) != null) {
            objectNode.put(STATUS, "ERROR! Similar record exists.");
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(null);
        }
        Student newStudent = new Student();
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setAge(age);
        newStudent.setSchool(school);
        studentRepository.save(newStudent);
        objectNode.put(ID, newStudent.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> updateStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(ID)) {
            objectNode.put(STATUS, "ERROR! `id` is a required field.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        long id = jsonNode.get(ID).asLong();
        Student student = studentRepository.findById(id);
        if (student == null) {
            objectNode.put(STATUS, "ERROR! `id` " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (jsonNode.has(FIRST_NAME)) {
            student.setFirstName(jsonNode.get(FIRST_NAME).asText());
        }
        if (jsonNode.has(LAST_NAME)) {
            student.setLastName(jsonNode.get(LAST_NAME).asText());
        }
        if (jsonNode.has(AGE)) {
            student.setAge(jsonNode.get(AGE).asInt());
        }
        if (jsonNode.has(SCHOOL_ID)) {
            long schoolId = jsonNode.get("school_id").asLong();
            School school = schoolRepository.findOne(schoolId);
            if (school == null) {
                objectNode.put(STATUS, "ERROR! `school_id` " + schoolId + " does not exist.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
            }
            student.setSchool(school);
        }
        studentRepository.save(student);
        objectNode.put(FIRST_NAME, student.getFirstName());
        objectNode.put(LAST_NAME, student.getLastName());
        objectNode.put(AGE, student.getAge());
        objectNode.put(SCHOOL_ID, student.getSchool().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> deleteStudentData(long id) {
        Student student = studentRepository.findById(id);
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (student == null) {
            objectNode.put(STATUS, "ERROR! Id: " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        studentRepository.delete(student);
        objectNode.put(STATUS, "SUCCESS! Id: " + id + " deleted.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
